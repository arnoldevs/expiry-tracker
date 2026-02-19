package com.carozzi.expirytracker.infrastructure.adapters.in.web;

import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase;
import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase.CreateProductCommand;
import com.carozzi.expirytracker.application.ports.in.FindProductUseCase;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.infrastructure.adapters.in.web.dtos.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}