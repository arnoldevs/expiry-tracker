package io.app.expirytracker.infrastructure.adapters.out.persistence;

import jakarta.persistence.EnumType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import io.app.expirytracker.application.ports.out.ProductRepositoryPort;
import io.app.expirytracker.domain.model.PaginatedResult;
import io.app.expirytracker.domain.model.Product;
import io.app.expirytracker.domain.model.ProductSearchCriteria;
import io.app.expirytracker.domain.model.ProductStatus;
import io.app.expirytracker.infrastructure.adapters.out.persistence.entities.ProductEntity;
import io.app.expirytracker.infrastructure.adapters.out.persistence.mappers.ProductMapper;
import io.app.expirytracker.infrastructure.adapters.out.persistence.repositories.JpaProductRepository;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

	private final JpaProductRepository jpaProductRepository;
	private final ProductMapper productMapper;

	@Override
	public Product save(Product product) {
		var entity = productMapper.toEntity(product);
		var savedEntity = jpaProductRepository.save(entity);
		return productMapper.toDomain(savedEntity);
	}

	@Override
	public Optional<Product> findById(UUID id) {
		return jpaProductRepository.findById(id)
				.filter(entity -> entity.getStatus() == ProductStatus.ACTIVE)
				.map(productMapper::toDomain);
	}

	@Override
	public Optional<Product> findByEan13(String ean13) {
		return jpaProductRepository.findByEan13(ean13)
				.filter(entity -> entity.getStatus() == ProductStatus.ACTIVE)
				.map(productMapper::toDomain);
	}

	@Override
	public boolean existsByEan13AndBatchNumber(String ean13, String batchNumber) {
		return jpaProductRepository.existsByEan13AndBatchNumber(ean13, batchNumber);
	}

	@Override
	public PaginatedResult<Product> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ProductEntity> productPage = jpaProductRepository.findAll(pageable);
		return toPaginatedResult(productPage);
	}

	@Override
	public boolean existsById(UUID id) {
		// Delegamos directamente al JpaRepository
		return jpaProductRepository.existsById(id);
	}

	/**
	 * Realiza un "Soft Delete" del producto.
	 * * En lugar de eliminar la fila física, recuperamos la entidad y
	 * actualizamos su estado a DISCARDED.
	 * * @param id Identificador único del producto.
	 */
	@Override
	public void deleteById(UUID id) {
		jpaProductRepository.findById(id).ifPresent(entity -> {
			entity.setStatus(ProductStatus.DISCARDED);
			jpaProductRepository.save(entity);
		});
	}

	/**
	 * Ejecuta una consulta dinámica basada en criterios opcionales.
	 * * Lógica de Seguridad:
	 * 1. Si el criterio incluye un estado específico (ej. DISCARDED), se filtra por
	 * ese.
	 * 2. Si el estado es null, se aplica por defecto ProductStatus.ACTIVE para
	 * evitar mostrar productos eliminados en búsquedas generales.
	 */
	@Override
	public PaginatedResult<Product> findByCriteria(ProductSearchCriteria criteria) {
		// Paginación: Usar valores del criterio o defaults.
		int page = (criteria.page() != null && criteria.page() >= 0) ? criteria.page() : 0;
		int size = (criteria.size() != null && criteria.size() > 0) ? criteria.size() : 10;
		Pageable pageable = PageRequest.of(page, size);

		// Determinamos el estado objetivo (Seguridad por defecto)
		ProductStatus targetStatus = (criteria.status() != null)
				? criteria.status()
				: ProductStatus.ACTIVE;

		// Iniciamos la especificación con el estado
		Specification<ProductEntity> spec = Specification.where(statusEqual(targetStatus));

		spec = spec.and(nameLike(criteria.name()))
				.and(eanEqual(criteria.ean()))
				.and(batchEqual(criteria.batch()))
				.and(isExpired(criteria.isExpired()))
				.and(expiredBefore(criteria.expiredBefore()))
				.and(isAboutToExpire(criteria.daysThreshold()));

		// Ejecutamos la consulta paginada y mapeamos a dominio
		Page<ProductEntity> productPage = jpaProductRepository.findAll(spec, pageable);
		return toPaginatedResult(productPage);
	}

	/**
	 * Convierte un objeto {@link Page} de Spring Data a nuestro
	 * {@link PaginatedResult} del dominio.
	 * 
	 * @param page El objeto Page que contiene los resultados de la consulta y la
	 *             información de paginación.
	 * @return Un PaginatedResult con los datos mapeados al modelo de dominio.
	 */
	private PaginatedResult<Product> toPaginatedResult(Page<ProductEntity> page) {
		var products = page.getContent().stream()
				.map(productMapper::toDomain)
				.toList();
		return new PaginatedResult<>(
				products,
				page.getTotalElements(),
				page.getTotalPages(),
				page.getNumber(),
				page.hasNext(),
				page.hasPrevious());
	}

	/**
	 * Crea una especificación para filtrar por el estado del producto en la base de
	 * datos.
	 * *
	 * <p>
	 * Se utiliza {@link EnumType#STRING} en la persistencia, por lo que esta
	 * especificación
	 * genera una comparación directa contra la columna 'status'.
	 * </p>
	 *
	 * @param status El estado deseado (ej. ACTIVE, SOLD, DISCARDED).
	 * @return Una {@link Specification} que representa la restricción de igualdad
	 *         por estado.
	 */
	private Specification<ProductEntity> statusEqual(ProductStatus status) {
		return (root, query, cb) -> (status == null) ? null
				: cb.equal(root.get("status"), status);
	}

	/**
	 * Filtra productos que vencerán dentro de un umbral de días.
	 * Lógica: hoy <= expiryDate < (hoy + days)
	 */
	private Specification<ProductEntity> isAboutToExpire(Integer days) {
		return (root, query, cb) -> {
			if (days == null || days < 0)
				return null;

			LocalDate today = LocalDate.now();
			LocalDate limitDate = today.plusDays(days);

			// Genera: WHERE expiry_date >= today AND expiry_date < limitDate
			return cb.and(
					cb.greaterThanOrEqualTo(root.get("expiryDate"), today),
					cb.lessThan(root.get("expiryDate"), limitDate));
		};
	}

	/**
	 * Crea una especificación para búsqueda parcial por nombre (Fuzzy Search).
	 * Se aplica lower() tanto al campo de la DB como al criterio para ignorar
	 * mayúsculas.
	 */
	private Specification<ProductEntity> nameLike(String name) {
		return (root, query, cb) -> (name == null || name.isBlank()) ? null
				: cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	/**
	 * Crea una especificación para búsqueda exacta por EAN-13.
	 */
	private Specification<ProductEntity> eanEqual(String ean) {
		return (root, query, cb) -> (ean == null || ean.isBlank()) ? null
				: cb.equal(root.get("ean13"), ean);
	}

	/**
	 * Crea una especificación para búsqueda exacta por número de lote.
	 */
	private Specification<ProductEntity> batchEqual(String batch) {
		return (root, query, cb) -> (batch == null || batch.isBlank()) ? null
				: cb.equal(root.get("batchNumber"), batch);
	}

	/**
	 * Filtra productos comparando la fecha de expiración con la fecha actual del
	 * sistema.
	 */
	private Specification<ProductEntity> isExpired(Boolean isExpired) {
		return (root, query, cb) -> {
			if (isExpired == null)
				return null;
			LocalDate today = LocalDate.now();
			return isExpired
					? cb.lessThan(root.get("expiryDate"), today) // Vencido: fecha < hoy
					: cb.greaterThanOrEqualTo(root.get("expiryDate"), today); // Vigente: fecha >= hoy
		};
	}

	/**
	 * Filtra productos que vencen antes o en la fecha especificada.
	 * Útil para reportes de "productos a vencer en los próximos X días".
	 */
	private Specification<ProductEntity> expiredBefore(LocalDate date) {
		return (root, query, cb) -> (date == null) ? null
				: cb.lessThanOrEqualTo(root.get("expiryDate"), date);
	}

}