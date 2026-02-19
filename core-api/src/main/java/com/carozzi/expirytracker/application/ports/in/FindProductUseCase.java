package com.carozzi.expirytracker.application.ports.in;

import com.carozzi.expirytracker.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindProductUseCase {

	/**
	 * Busca un producto por su identificador único UUID v7.
	 * 
	 * @param id Identificador del producto.
	 * @return Un Optional con el producto si existe.
	 */
	Optional<Product> findById(UUID id);

	/**
	 * Recupera todos los productos del inventario.
	 * 
	 * @return Lista de productos (modelos de dominio).
	 */
	List<Product> findAll();
}