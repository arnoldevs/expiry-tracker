package com.carozzi.expirytracker.domain.model;

import java.time.LocalDate;

/**
 * Criterios de búsqueda para productos.
 * Encapsula la lógica de validación de los filtros en el dominio.
 */
public record ProductSearchCriteria(
		String name,
		String ean,
		String batch,
		LocalDate expiredBefore,
		Boolean isExpired,
		Integer daysThreshold,
		ProductStatus status,
		Integer page,
		Integer size) {

	/**
	 * Crea una instancia con todos los filtros en null.
	 * Útil para búsquedas globales donde se aplicarán los filtros por defecto.
	 */
	public static ProductSearchCriteria empty() {
		return new ProductSearchCriteria(null, null, null, null, null, null, null, null, null);
	}

	/**
	 * Determina si el criterio no tiene información útil para realizar una
	 * búsqueda.
	 * 
	 * @return true si todos los campos son nulos o están vacíos.
	 */
	public boolean isInvalid() {
		return (name == null || name.isBlank()) &&
				(ean == null || ean.isBlank()) &&
				(batch == null || batch.isBlank()) &&
				expiredBefore == null &&
				isExpired == null &&
				daysThreshold == null &&
				status == null;

	}
}