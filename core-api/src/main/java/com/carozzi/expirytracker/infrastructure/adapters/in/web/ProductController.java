package com.carozzi.expirytracker.infrastructure.adapters.in.web;

import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase;
import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase.CreateProductCommand;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.infrastructure.adapters.in.web.dtos.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final CreateProductUseCase createProductUseCase;

	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
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
}