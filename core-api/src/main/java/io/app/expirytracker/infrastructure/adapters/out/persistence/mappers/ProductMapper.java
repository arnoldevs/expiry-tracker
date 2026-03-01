package io.app.expirytracker.infrastructure.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import io.app.expirytracker.domain.model.Product;
import io.app.expirytracker.infrastructure.adapters.out.persistence.entities.ProductEntity;

@Component // Lo marcamos como componente para que Spring lo pueda inyectar
public class ProductMapper {

	// De Entidad (Base de Datos) a Dominio (Corazón)
	public Product toDomain(ProductEntity entity) {
		if (entity == null)
			return null;

		return new Product(
				entity.getId(),
				entity.getEan13(),
				entity.getName(),
				entity.getBatchNumber(),
				entity.getExpiryDate(),
				entity.getQuantity(),
				entity.getCategory(),
				entity.getStatus());
	}

	// De Dominio (Corazón) a Entidad (Base de Datos)
	public ProductEntity toEntity(Product domain) {
		if (domain == null)
			return null;

		return ProductEntity.builder()
				.id(domain.id())
				.ean13(domain.ean13())
				.name(domain.name())
				.batchNumber(domain.batchNumber())
				.expiryDate(domain.expiryDate())
				.quantity(domain.quantity())
				.category(domain.category())
				.status(domain.status())
				.build();
	}
}