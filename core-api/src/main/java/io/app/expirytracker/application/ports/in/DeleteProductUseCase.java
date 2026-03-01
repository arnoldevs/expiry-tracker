package io.app.expirytracker.application.ports.in;

import java.util.UUID;

public interface DeleteProductUseCase {
	/**
	 * Da de baja un producto del inventario activo.
	 * 
	 * @param id Identificador único del producto.
	 */
	void delete(UUID id);
}