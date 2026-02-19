package com.carozzi.expirytracker.application.ports.in;

import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;

import java.util.List;
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
	 * Recupera todos los productos del inventario.
	 * 
	 * @return Lista de productos (modelos de dominio).
	 */
	List<Product> findAll();

	/**
	 * Ejecuta la búsqueda de productos basada en los criterios de filtrado.
	 *
	 * @param criteria Contenedor de filtros opcionales proporcionados por el
	 *                 usuario.
	 * @return Listado de productos encontrados.
	 */
	List<Product> execute(ProductSearchCriteria criteria);
}