package com.carozzi.expirytracker.application.ports.out;

import com.carozzi.expirytracker.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de Salida para Productos.
 * Define las operaciones que la Aplicación necesita, sin importar
 * si usamos Postgres, MySQL o un archivo de texto.
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

  // Listar todos
  List<Product> findAll();

  // Eliminar
  void deleteById(UUID id);
}
