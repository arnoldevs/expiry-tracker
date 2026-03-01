package io.app.expirytracker.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

import io.app.expirytracker.domain.model.ProductStatus;
import lombok.*;

@Entity
@Table(name = "products", uniqueConstraints = {
		@UniqueConstraint(name = "uk_product_batch", columnNames = { "ean13", "batch_number" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity extends AuditableEntity {

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

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private ProductStatus status;

	/*
	 * TODO: Future Refactoring for Collections
	 * Actualmente usamos la implementación default de equals/hashCode (identidad de
	 * memoria).
	 * Si en el futuro se agregan relaciones @OneToMany (Set<ProductEntity>),
	 * SE DEBE implementar equals/hashCode basado estrictamente en el ID (Business
	 * Key).
	 */
}