package io.app.expirytracker.domain.model;

/**
 * Representa el estado administrativo y de trazabilidad del producto.
 */
public enum ProductStatus {
	/** El producto está en stock y disponible. */
	ACTIVE,

	/** El producto fue vendido o entregado exitosamente. */
	SOLD,

	/** El producto fue retirado por vencimiento, daño o merma. */
	DISCARDED
}