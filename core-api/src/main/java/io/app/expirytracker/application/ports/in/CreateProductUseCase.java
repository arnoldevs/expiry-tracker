package io.app.expirytracker.application.ports.in;

import java.time.LocalDate;

import io.app.expirytracker.domain.model.Product;

public interface CreateProductUseCase {

	// Contrato: Recibe los datos necesarios, devuelve el Producto creado
	Product create(CreateProductCommand command);

	// El "Command" solo tiene lo que el usuario/sistema externo provee
	record CreateProductCommand(
			String ean13,
			String name,
			String batchNumber,
			LocalDate expiryDate,
			Integer quantity,
			String category) {
	}
}