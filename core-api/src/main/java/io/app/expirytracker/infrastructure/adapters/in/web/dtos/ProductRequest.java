package io.app.expirytracker.infrastructure.adapters.in.web.dtos;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

/**
 * DTO para recibir datos desde el exterior.
 */
public record ProductRequest(
		@NotBlank(message = "El código EAN-13 es obligatorio") @Pattern(regexp = "\\d{13}", message = "El EAN-13 debe tener exactamente 13 dígitos") String ean13,

		@NotBlank(message = "El nombre del producto es obligatorio") String name,

		@NotBlank(message = "El número de lote es obligatorio") String batchNumber,

		@NotNull(message = "La fecha de vencimiento es obligatoria") @FutureOrPresent(message = "La fecha de vencimiento no puede ser una fecha pasada") LocalDate expiryDate,

		@NotNull(message = "La cantidad es obligatoria") @Min(value = 0, message = "El stock no puede ser negativo") Integer quantity,

		@NotBlank(message = "La categoría es obligatoria") String category) {
}