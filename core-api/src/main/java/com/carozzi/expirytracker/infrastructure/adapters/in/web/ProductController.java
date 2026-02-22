package com.carozzi.expirytracker.infrastructure.adapters.in.web;

import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase;
import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase.CreateProductCommand;
import com.carozzi.expirytracker.application.ports.in.FindProductUseCase;
import com.carozzi.expirytracker.application.ports.in.DeleteProductUseCase;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.domain.model.ProductStatus;
import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;
import com.carozzi.expirytracker.infrastructure.adapters.in.web.dtos.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final CreateProductUseCase createProductUseCase;
	private final FindProductUseCase findProductUseCase;
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
	 * Obtiene todos los productos ACTIVOS del inventario.
	 * Gracias al filtro base en el PersistenceAdapter, este método ya no
	 * devolverá productos SOLD o DISCARDED por error.
	 */
	@GetMapping
	public ResponseEntity<List<Product>> getAll() {
		List<Product> products = findProductUseCase.findAll();
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
	 * Realiza una búsqueda avanzada de productos aplicando múltiples filtros
	 * opcionales.
	 * La lógica interna utiliza JPA Specifications para generar una consulta SQL
	 * dinámica.
	 * *
	 * <p>
	 * Ejemplos de uso:
	 * </p>
	 * <ul>
	 * <li>Buscar fideos por nombre: {@code GET /search?name=fideo}</li>
	 * <li>Buscar productos descartados: {@code GET /search?status=DISCARDED}</li>
	 * <li>Buscar lotes específicos vencidos:
	 * {@code GET /search?batch=L123&isExpired=true}</li>
	 * </ul>
	 *
	 * @param name   Filtro parcial por nombre.
	 * @param ean    Filtro exacto por EAN-13.
	 * @param batch  Filtro exacto por lote.
	 * @param status Filtro por estado del producto (ACTIVE, SOLD, DISCARDED).
	 * @return Lista de productos filtrados.
	 * @param expiredBefore Fecha límite de vencimiento para búsqueda.
	 * @param isExpired     Filtro booleano para obtener solo vencidos o solo
	 *                      vigentes.
	 * @param daysThreshold Umbral de días para búsqueda por proximidad.
	 * @return Una {@link ResponseEntity} que contiene la lista de {@link Product}.
	 */
	@GetMapping("/search")
	public ResponseEntity<List<Product>> search(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String ean,
			@RequestParam(required = false) String batch,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiredBefore,
			@RequestParam(required = false) Boolean isExpired,
			@RequestParam(required = false) Integer daysThreshold,
			@RequestParam(required = false) ProductStatus status) {

		// Construimos el record de dominio incluyendo el campo status
		var criteria = new ProductSearchCriteria(name, ean, batch, expiredBefore, isExpired, daysThreshold, status);

		List<Product> results = findProductUseCase.execute(criteria);
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
}