package com.carozzi.expirytracker.application.services;

import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase;
import com.carozzi.expirytracker.application.ports.out.ProductRepositoryPort;
import com.carozzi.expirytracker.domain.model.Product;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase {

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
}