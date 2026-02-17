package com.carozzi.expirytracker.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "products", uniqueConstraints = {
		@UniqueConstraint(name = "uk_product_batch", columnNames = { "ean13", "batch_number" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false, length = 13)
	private String ean13;

	@Column(nullable = false)
	private String name;

	@Column(name = "batch_number", nullable = false)
	private String batchNumber;

	@Column(name = "expiry_date", nullable = false)
	private LocalDate expiryDate;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private String category;
}