package com.carozzi.expirytracker.infrastructure.adapters.out.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.entities.ProductEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface JpaProductRepository
		extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {

	// Buscar por código de barras (EAN-13)
	Optional<ProductEntity> findByEan13(String ean13);

	// Spring genera: SELECT count(*) > 0 FROM products WHERE ean13 = ? AND
	// batch_number = ?
	boolean existsByEan13AndBatchNumber(String ean13, String batchNumber);

	// Buscar todos los productos de una categoría específica
	List<ProductEntity> findByCategory(String category);

	// Buscar por número de lote para trazabilidad
	List<ProductEntity> findByBatchNumber(String batchNumber);
}