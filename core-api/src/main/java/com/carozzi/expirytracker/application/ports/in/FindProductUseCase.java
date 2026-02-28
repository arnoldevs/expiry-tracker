package com.carozzi.expirytracker.application.ports.in;

import com.carozzi.expirytracker.domain.model.PaginatedResult;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;

import java.util.Optional;
import java.util.UUID;

/**
 * Caso de Uso encargado de orquestar la búsqueda avanzada de productos.
 * Representa la lógica de negocio para filtrar el inventario.
 */
public interface FindProductUseCase {

	/**
	 * Busca un producto por su identificador único UUID v7.
	 * 
	 * @param id Identificador del producto.
	 * @return Un Optional con el producto si existe.
	 */
	Optional<Product> findById(UUID id);

	/**
	 * Recupera todos los productos del inventario de forma paginada.
	 *
	 * @param page El número de página a recuperar (base 0).
	 * @param size El tamaño de la página.
	 * @return Un contenedor con la lista de productos y la información de
	 *         paginación.
	 */
	PaginatedResult<Product> findAll(int page, int size);

	/**
	 * Ejecuta la búsqueda de productos basada en los criterios de filtrado y
	 * paginación.
	 *
	 * @param criteria Contenedor de filtros opcionales y paginación
	 *                 proporcionados por el usuario.
	 * @return Un contenedor con la lista de productos encontrados y la información
	 *         de paginación.
	 */
	PaginatedResult<Product> execute(ProductSearchCriteria criteria);
}