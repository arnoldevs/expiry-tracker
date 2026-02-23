package com.carozzi.expirytracker.application.ports.out;

import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de Salida para Productos.
 * Define las operaciones que la Aplicación necesita, sin importar
 * si usamos Postgres, MySQL o un archivo de texto.
 * Contrato que deben implementar los adaptadores de infraestructura (DB).
 */
public interface ProductRepositoryPort {

  // Guardar (Crear o Actualizar)
  Product save(Product product);

  // Buscar por ID (Técnico)
  Optional<Product> findById(UUID id);

  // Buscar por Código de Barras
  Optional<Product> findByEan13(String ean13);

  // Sirve para validar duplicidad de productos
  boolean existsByEan13AndBatchNumber(String ean13, String batchNumber);

  /**
   * Verifica si un producto existe en el sistema por su identificador único.
   * Fundamental para validaciones previas a la eliminación o actualización.
   */
  boolean existsById(UUID id);

  // Listar todos
  List<Product> findAll();

  // Eliminar
  void deleteById(UUID id);

  /**
   * Recupera una lista de productos que coincidan con los criterios
   * especificados.
   *
   * @param criteria Objeto que contiene los filtros (EAN, lote, nombre, etc.).
   *                 Si un campo es null, se debe omitir en la consulta.
   * @return Una lista de {@link Product} que cumplen los criterios,
   *         o una lista vacía si no hay coincidencias.
   */
  List<Product> findByCriteria(ProductSearchCriteria criteria);
}
