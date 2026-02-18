package com.carozzi.expirytracker.application.ports.in;

import com.carozzi.expirytracker.domain.model.Product;
import java.time.LocalDate;

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