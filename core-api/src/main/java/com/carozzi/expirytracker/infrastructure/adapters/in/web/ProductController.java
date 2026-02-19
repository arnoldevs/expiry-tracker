package com.carozzi.expirytracker.infrastructure.adapters.in.web;

import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase;
import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase.CreateProductCommand;
import com.carozzi.expirytracker.application.ports.in.FindProductUseCase;
import com.carozzi.expirytracker.domain.model.Product;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final CreateProductUseCase createProductUseCase;
	private final FindProductUseCase findProductUseCase;

	@PostMapping
	public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
		// Mapeo manual del DTO al Comando de Aplicación
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
	 * Obtiene todos los productos del inventario.
	 * El campo 'isExpired' se calcula dinámicamente en el modelo de dominio.
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
				.orElseThrow(() -> new java.util.NoSuchElementException("No se encontró el producto con ID: " + id));
	}

	/**
	 * Búsqueda avanzada de productos con filtros opcionales.
	 * Ejemplo de uso: GET /api/products/search?name=fideo&batch=L123
	 *
	 * @param name  Filtro parcial por nombre.
	 * @param ean   Filtro exacto por EAN-13.
	 * @param batch Filtro exacto por lote.
	 * @return Lista de productos filtrados.
	 * @param expiredBefore Fecha límite de vencimiento para búsqueda.
	 * @param isExpired     Filtro booleano para obtener solo vencidos o solo
	 *                      vigentes.
	 */
	@GetMapping("/search")
	public ResponseEntity<List<Product>> search(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String ean,
			@RequestParam(required = false) String batch,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiredBefore,
			@RequestParam(required = false) Boolean isExpired) {

		// Construimos el record de dominio con todos los parámetros
		var criteria = new ProductSearchCriteria(name, ean, batch, expiredBefore, isExpired);

		// Ejecutamos el caso de uso
		// El servicio lanzará IllegalArgumentException si criteria.isInvalid()
		List<Product> results = findProductUseCase.execute(criteria);

		return ResponseEntity.ok(results);
	}
}