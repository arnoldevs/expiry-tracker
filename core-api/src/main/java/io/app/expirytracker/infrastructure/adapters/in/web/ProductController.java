package io.app.expirytracker.infrastructure.adapters.in.web;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.app.expirytracker.application.ports.in.CreateProductUseCase;
import io.app.expirytracker.application.ports.in.DeleteProductUseCase;
import io.app.expirytracker.application.ports.in.FindProductUseCase;
import io.app.expirytracker.application.ports.in.UpdateProductUseCase;
import io.app.expirytracker.application.ports.in.CreateProductUseCase.CreateProductCommand;
import io.app.expirytracker.application.ports.in.UpdateProductUseCase.UpdateProductCommand;
import io.app.expirytracker.domain.model.PaginatedResult;
import io.app.expirytracker.domain.model.Product;
import io.app.expirytracker.domain.model.ProductSearchCriteria;
import io.app.expirytracker.domain.model.ProductStatus;
import io.app.expirytracker.infrastructure.adapters.in.web.dtos.ProductRequest;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final CreateProductUseCase createProductUseCase;
	private final FindProductUseCase findProductUseCase;
	private final UpdateProductUseCase updateProductUseCase;
	private final DeleteProductUseCase deleteProductUseCase;

	@PostMapping
	public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
		var command = new CreateProductCommand(
				request.ean13(),
				request.name(),
				request.batchNumber(),
				request.expiryDate(),
				request.quantity(),
				request.category());

		Product createdProduct = createProductUseCase.create(command);
		return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
	}

	/**
	 * Obtiene todos los productos del inventario de forma paginada.
	 * No aplica ningún filtro por defecto.
	 */
	@GetMapping
	public ResponseEntity<PaginatedResult<Product>> getAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		PaginatedResult<Product> products = findProductUseCase.findAll(page, size);
		return ResponseEntity.ok(products);
	}

	/**
	 * Busca un producto por su UUID v7.
	 * Si no existe, lanza NoSuchElementException que es capturada por el
	 * GlobalExceptionHandler.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Product> getById(@PathVariable UUID id) {
		return findProductUseCase.findById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new NoSuchElementException("No se encontró el producto con ID: " + id));
	}

	/**
	 * Realiza una búsqueda avanzada y paginada de productos aplicando múltiples
	 * filtros opcionales.
	 * Por defecto, si no se especifican filtros, solo busca productos ACTIVOS.
	 * <p>
	 * Ejemplos:
	 * {@code /search?name=fideo&page=0&size=20}
	 * {@code /search?status=DISCARDED}
	 *
	 * @param page          Número de la página a obtener (base 0).
	 * @param size          Tamaño de la página.
	 * @param name          Filtro parcial por nombre.
	 * @param ean           Filtro exacto por EAN-13.
	 * @param batch         Filtro exacto por lote.
	 * @param expiredBefore Fecha límite de vencimiento.
	 * @param isExpired     Filtro para obtener vencidos o vigentes.
	 * @param daysThreshold Umbral de días para búsqueda por proximidad a vencer.
	 * @param status        Filtro por estado del producto.
	 * @return Una {@link ResponseEntity} que contiene el resultado paginado.
	 */
	@GetMapping("/search")
	public ResponseEntity<PaginatedResult<Product>> search(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String ean,
			@RequestParam(required = false) String batch,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiredBefore,
			@RequestParam(required = false) Boolean isExpired,
			@RequestParam(required = false) Integer daysThreshold,
			@RequestParam(required = false) ProductStatus status,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size) {

		var criteria = new ProductSearchCriteria(name, ean, batch, expiredBefore, isExpired, daysThreshold, status, page,
				size);

		PaginatedResult<Product> results = findProductUseCase.execute(criteria);
		return ResponseEntity.ok(results);
	}

	/**
	 * Endpoint para realizar el Soft Delete (Descarte) de un producto.
	 * Aunque internamente cambia el estado a DISCARDED, seguimos
	 * la convención REST de usar DELETE.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		deleteProductUseCase.delete(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Actualiza un producto existente por su ID.
	 * Es idempotente: si envías los mismos datos varias veces, el resultado es el
	 * mismo.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(
			@PathVariable UUID id,
			@Valid @RequestBody ProductRequest request) {

		var command = new UpdateProductCommand(
				request.ean13(),
				request.name(),
				request.batchNumber(),
				request.expiryDate(),
				request.quantity(),
				request.category());

		Product updatedProduct = updateProductUseCase.update(id, command);
		return ResponseEntity.ok(updatedProduct);
	}
}