package com.carozzi.expirytracker.domain.model;

import java.util.List;

/**
 * Un contenedor genérico para resultados de consultas paginadas.
 * Este record es inmutable y transporta tanto los datos de la página actual
 * como los metadatos de paginación.
 *
 * @param <T>           El tipo de los datos en la lista.
 * @param data          La lista de elementos para la página actual.
 * @param totalElements El número total de elementos disponibles en todas las
 *                      páginas.
 * @param totalPages    El número total de páginas.
 * @param currentPage   El índice de la página actual (base 0).
 * @param hasNext       Indica si hay una página siguiente.
 * @param hasPrevious   Indica si hay una página anterior.
 */
public record PaginatedResult<T>(
                List<T> data,
                long totalElements,
                int totalPages,
                int currentPage,
                boolean hasNext,
                boolean hasPrevious) {
}
