package com.carozzi.expirytracker.infrastructure.adapters.out.persistence;

import com.carozzi.expirytracker.application.ports.out.ProductRepositoryPort;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.mappers.ProductMapper;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.repositories.JpaProductRepository;

import jakarta.persistence.EnumType;

import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

import com.carozzi.expirytracker.domain.model.ProductStatus;

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
	public List<Product> findAll() {
		return findByCriteria(ProductSearchCriteria.empty());
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
	public List<Product> findByCriteria(ProductSearchCriteria criteria) {

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

		// Ejecutamos y mapeamos a dominio
		return jpaProductRepository.findAll(spec).stream()
				.map(productMapper::toDomain)
				.toList();
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