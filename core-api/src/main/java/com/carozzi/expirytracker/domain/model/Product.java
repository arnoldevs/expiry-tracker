package com.carozzi.expirytracker.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record Product(
    // Usamos UUID por dos razones clave:
    // 1. Seguridad: No es secuencial, lo que evita que adivinen otros IDs
    // (Enumeration Attack).
    // 2. Independencia: Permite generar el ID en la aplicación sin esperar a que la
    // Base de Datos lo asigne.
    UUID id,

    String ean13, // Código de barras universal (ej: 780...)
    String name,
    String batchNumber, // Lote para trazabilidad
    LocalDate expiryDate,
    Integer quantity, // Stock disponible
    String category, // Categoría (Salsas, Pastas, Mascotas, Congelados, etc.)
    ProductStatus status // Estado de trazabilidad
) {

  // Constructor Compacto
  public Product {
    // Validaciones de Existencia
    if (id == null)
      throw new IllegalArgumentException("El ID del producto es obligatorio");
    if (expiryDate == null)
      throw new IllegalArgumentException("La fecha de vencimiento es obligatoria");
    if (quantity == null)
      throw new IllegalArgumentException("La cantidad no puede ser nula");
    if (status == null)
      throw new IllegalArgumentException("El estado del producto es obligatorio");

    // Validaciones de Texto
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
    }
    if (ean13 == null || !ean13.matches("\\d{13}")) {
      throw new IllegalArgumentException("El EAN-13 debe tener exactamente 13 dígitos numéricos");
    }
    if (batchNumber == null || batchNumber.isBlank()) {
      throw new IllegalArgumentException("El número de lote no puede estar vacío");
    }
    if (category == null || category.isBlank()) {
      throw new IllegalArgumentException("La categoría es obligatoria");
    }

    // Validaciones de Lógica de Negocio
    if (quantity < 0) {
      throw new IllegalArgumentException("El stock no puede ser negativo");
    }
  }

  // --- LÓGICA DE DOMINIO ---

  public boolean isExpired() {
    return LocalDate.now().isAfter(this.expiryDate);
  }

  /**
   * Solo nos interesa alertar si el producto
   * está ACTIVE. Si ya fue SOLD o DISCARDED, la alerta no tiene sentido.
   */
  public boolean isAboutToExpire(int daysThreshold) {
    if (this.status != ProductStatus.ACTIVE) {
      return false;
    }
    LocalDate today = LocalDate.now();
    LocalDate warningDate = today.plusDays(daysThreshold);
    return !isExpired() && this.expiryDate.isBefore(warningDate);
  }
}
