package com.carozzi.expirytracker.infrastructure.adapters.out.persistence;

import com.carozzi.expirytracker.application.ports.out.ProductRepositoryPort;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.mappers.ProductMapper;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.repositories.JpaProductRepository;

import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

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
				.map(productMapper::toDomain);
	}

	@Override
	public Optional<Product> findByEan13(String ean13) {
		return jpaProductRepository.findByEan13(ean13)
				.map(productMapper::toDomain);
	}

	@Override
	public boolean existsByEan13AndBatchNumber(String ean13, String batchNumber) {
		return jpaProductRepository.existsByEan13AndBatchNumber(ean13, batchNumber);
	}

	@Override
	public List<Product> findAll() {
		return jpaProductRepository.findAll()
				.stream()
				.map(productMapper::toDomain)
				.toList();
	}

	@Override
	public void deleteById(UUID id) {
		jpaProductRepository.deleteById(id);
	}

	/**
	 * Implementa la búsqueda dinámica utilizando JPA Specifications.
	 * Se ha optado por esta aproximación para evitar la explosión de métodos
	 * en el repositorio y manejar múltiples filtros opcionales de forma limpia.
	 *
	 * @param criteria Objeto de dominio con los filtros aplicados por el usuario.
	 * @return Lista de productos que cumplen con todos los criterios
	 *         proporcionados.
	 */
	@Override
	public List<Product> findByCriteria(ProductSearchCriteria criteria) {
		// Empezamos directamente con el primer filtro.
		// Si nameLike devuelve null, Specification.where lo maneja correctamente.
		Specification<ProductEntity> spec = Specification.where(nameLike(criteria.name()));

		// Luego seguimos con los .and()
		spec = spec.and(eanEqual(criteria.ean()))
				.and(batchEqual(criteria.batch()))
				.and(isExpired(criteria.isExpired()))
				.and(expiredBefore(criteria.expiredBefore()))
				.and(isAboutToExpire(criteria.daysThreshold()));

		return jpaProductRepository.findAll(spec).stream()
				.map(productMapper::toDomain)
				.toList();
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