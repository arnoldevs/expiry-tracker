package com.carozzi.expirytracker.infrastructure.adapters.in.web.dtos;

import java.time.LocalDate;

/**
 * DTO para recibir datos desde el exterior.
 * Nota que no incluimos el ID porque usamos UUID v7 generado en el servicio.
 */
public record ProductRequest(
        String ean13,
        String name,
        String batchNumber,
        LocalDate expiryDate,
        Integer quantity,
        String category) {
}