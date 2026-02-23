package com.carozzi.expirytracker.application.ports.in;

import com.carozzi.expirytracker.domain.model.Product;
import java.time.LocalDate;
import java.util.UUID;

public interface UpdateProductUseCase {

	/**
	 * Actualiza los datos descriptivos de un producto existente.
	 * No permite cambiar el ID ni el Estado.
	 */
	Product update(UUID id, UpdateProductCommand command);

	record UpdateProductCommand(
			String ean13,
			String name,
			String batchNumber,
			LocalDate expiryDate,
			Integer quantity,
			String category) {
	}
}