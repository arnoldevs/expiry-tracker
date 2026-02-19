package com.carozzi.expirytracker.application.services;

import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase;
import com.carozzi.expirytracker.application.ports.in.FindProductUseCase;
import java.util.List;
import java.util.Optional;
import com.carozzi.expirytracker.application.ports.out.ProductRepositoryPort;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase, FindProductUseCase {

	private final ProductRepositoryPort productRepository;

	@Override
	@Transactional
	public Product create(CreateProductCommand command) {
		// 1. Validar integridad: No duplicar lotes del mismo producto
		if (productRepository.existsByEan13AndBatchNumber(command.ean13(), command.batchNumber())) {
			throw new IllegalArgumentException(
					String.format("Ya existe un registro para el producto [%s] con el lote [%s].",
							command.ean13(), command.batchNumber()));
		}

		// Generar identidad UUID v7
		UUID productId = Generators.timeBasedEpochGenerator().generate();

		// Mapear a Dominio (el record valida integridad interna)
		Product product = new Product(
				productId,
				command.ean13(),
				command.name(),
				command.batchNumber(),
				command.expiryDate(),
				command.quantity(),
				command.category());

		// Persistencia
		return productRepository.save(product);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Product> findById(UUID id) {
		return productRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	/**
	 * Ejecuta la búsqueda avanzada de productos.
	 * * @param criteria Criterios de filtrado proporcionados por el cliente.
	 * 
	 * @return Lista de productos que coinciden con los filtros.
	 * @throws IllegalArgumentException si no se proporciona ningún filtro válido.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Product> execute(ProductSearchCriteria criteria) {
		if (criteria == null || criteria.isInvalid()) {
			throw new IllegalArgumentException("Debe proporcionar al menos un filtro para la búsqueda avanzada.");
		}

		return productRepository.findByCriteria(criteria);
	}
}