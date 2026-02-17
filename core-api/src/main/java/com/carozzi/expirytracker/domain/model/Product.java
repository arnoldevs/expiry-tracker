package com.carozzi.expirytracker.domain.model;

import java.time.LocalDate;
import java.util.Objects;
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
    String category // Categoría (Salsas, Pastas, Mascotas, Congelados, etc.)
) {

  // Constructor Compacto
  public Product {
    // 1. Validaciones de Integridad
    Objects.requireNonNull(id, "El ID del producto es obligatorio");
    Objects.requireNonNull(name, "El nombre del producto es obligatorio");
    Objects.requireNonNull(ean13, "El código EAN-13 es obligatorio");
    Objects.requireNonNull(batchNumber, "El número de lote es obligatorio");
    Objects.requireNonNull(expiryDate, "La fecha de vencimiento es obligatoria");
    Objects.requireNonNull(category, "La categoría es obligatoria");
    Objects.requireNonNull(quantity, "El stock no puede ser nulo");

    // 2. Validaciones de Formato
    if (!ean13.matches("\\d{13}")) {
      throw new IllegalArgumentException("El EAN-13 debe tener exactamente 13 dígitos numéricos");
    }

    if (quantity < 0) {
      throw new IllegalArgumentException("El stock no puede ser negativo");
    }
    if (name.isBlank()) {
      throw new IllegalArgumentException("El nombre no puede estar vacío");
    }
    if (batchNumber.isBlank()) {
      throw new IllegalArgumentException("El número de lote no puede estar vacío");
    }
  }

  // --- LÓGICA DE DOMINIO ---

  public boolean isExpired() {
    return LocalDate.now().isAfter(this.expiryDate);
  }

  public boolean isAboutToExpire(int daysThreshold) {
    LocalDate today = LocalDate.now();
    LocalDate warningDate = today.plusDays(daysThreshold);
    // Retorna true si NO ha vencido aún Y vence antes del umbral de aviso
    return !isExpired() && this.expiryDate.isBefore(warningDate);
  }
}
