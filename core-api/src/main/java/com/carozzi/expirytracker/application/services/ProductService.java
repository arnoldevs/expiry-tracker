package com.carozzi.expirytracker.application.services;

import com.carozzi.expirytracker.application.ports.in.CreateProductUseCase;
import com.carozzi.expirytracker.application.ports.in.FindProductUseCase;
import com.carozzi.expirytracker.application.ports.in.UpdateProductUseCase;
import com.carozzi.expirytracker.application.ports.in.DeleteProductUseCase;
import com.carozzi.expirytracker.application.ports.out.ProductRepositoryPort;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.domain.model.ProductStatus;
import com.carozzi.expirytracker.domain.model.ProductSearchCriteria;

import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Servicio que implementa la lógica de negocio para la gestión de productos.
 * * Se utiliza el patrón de diseño "Command" para la creación, asegurando que
 * la capa de aplicación no dependa de las entidades de persistencia ni
 * exponga directamente el modelo de dominio en la entrada.
 */
@Service
@RequiredArgsConstructor
public class ProductService
		implements CreateProductUseCase, FindProductUseCase, UpdateProductUseCase, DeleteProductUseCase {

	private final ProductRepositoryPort productRepository;

	/**
	 * Registra un nuevo producto en el sistema.
	 * * @param command Objeto DTO que contiene solo los datos permitidos para la
	 * creación.
	 * El uso de un 'Command' evita que el cliente intente manipular
	 * campos sensibles como el ID o el Status manualmente.
	 * * @return El producto creado con su ID y estado inicial asignado.
	 * 
	 * @throws IllegalArgumentException si el EAN-13 y Lote ya existen.
	 */
	@Override
	@Transactional
	public Product create(CreateProductCommand command) {
		// Validar duplicidad (Fail First)
		checkDuplicity(command.ean13(), command.batchNumber());

		// Transformamos el Command en un objeto de Dominio.
		// El record Product ya contiene las validaciones internas.
		Product product = mapToProduct(command);

		// Persistir
		return productRepository.save(product);
	}

	private void checkDuplicity(String ean, String batch) {
		if (productRepository.existsByEan13AndBatchNumber(ean, batch)) {
			throw new IllegalArgumentException(
					String.format("Ya existe un registro para el producto [%s] con el lote [%s].", ean, batch));
		}
	}

	/**
	 * Transforma un CreateProductCommand en un objeto de dominio Product.
	 * * Este método centraliza las reglas de "nacimiento" del producto:
	 * 1. Generación de identidad única (UUID).
	 * 2. Asignación del estado inicial (ProductStatus.ACTIVE).
	 * * Separar este mapeo asegura que la lógica de inicialización no ensucie
	 * el flujo principal del método create.
	 * * @param cmd Datos de entrada validados por el Use Case.
	 * * @return Una instancia de Product lista para ser procesada por el dominio.
	 */
	private Product mapToProduct(CreateProductCommand cmd) {
		return new Product(
				Generators.timeBasedEpochGenerator().generate(),
				cmd.ean13(),
				cmd.name(),
				cmd.batchNumber(),
				cmd.expiryDate(),
				cmd.quantity(),
				cmd.category(),
				ProductStatus.ACTIVE);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Product> findById(UUID id) {
		return productRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Product> findAll() {
		// Reutilizamos la lógica de execute para asegurar el filtro de activos
		return execute(ProductSearchCriteria.empty());
	}

	/**
	 * Ejecuta la búsqueda avanzada de productos.
	 * *
	 * <p>
	 * Si los criterios son nulos o inválidos, el sistema asume una búsqueda
	 * global y retorna solo los productos activos (equivalente a findAll).
	 * </p>
	 * * @param criteria Criterios de filtrado (nombre, ean, status, etc.).
	 * * @return Lista de productos que coinciden con los filtros.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Product> execute(ProductSearchCriteria criteria) {
		// Si el criterio es inválido o nulo, NO lanzamos error.
		// En su lugar, aplicamos el filtro de seguridad (ACTIVE)
		// y devolvemos los resultados.

		ProductSearchCriteria finalCriteria = (criteria == null || criteria.isInvalid())
				? ProductSearchCriteria.empty()
				: criteria;

		return productRepository.findByCriteria(finalCriteria);
	}

	/**
	 * Implementación del Caso de Uso para eliminar (descartar) un producto.
	 * * Aplica la regla de negocio "Fail First": no intentamos eliminar
	 * si el ID no corresponde a un registro existente.
	 * * @param id Identificador único del producto a dar de baja.
	 * * @throws NoSuchElementException si el producto no existe.
	 */
	@Override
	@Transactional // Asegura que el cambio de estado se confirme en DB
	public void delete(UUID id) {
		// Validamos existencia usando el método del puerto
		if (!productRepository.existsById(id)) {
			throw new NoSuchElementException(
					"No se puede realizar la eliminación: El producto con ID [" + id + "] no existe.");
		}

		// El Adapter se encargará de que sea Soft Delete.
		productRepository.deleteById(id);
	}

	/**
	 * Actualiza un producto existente.
	 * Valida reglas de negocio para asegurar la consistencia del inventario.
	 */
	@Override
	@Transactional
	public Product update(UUID id, UpdateProductCommand command) {
		// Recupera el producto actual (Fail Fast si no existe)
		Product currentProduct = productRepository.findById(id)
				.orElseThrow(
						() -> new NoSuchElementException("No se puede actualizar: El producto con ID [" + id + "] no existe."));

		// Boqueo de edición para productos no activos.
		if (currentProduct.status() != ProductStatus.ACTIVE) {
			throw new IllegalArgumentException(
					"No se puede editar un producto que no está ACTIVO (Estado actual: " + currentProduct.status() + ")");
		}

		// Valida Duplicidad (Solo si cambió EAN o Lote)
		// Si el EAN o el Lote son distintos a los que ya tenía, verificamos que la
		// nueva combinación no exista en otro lado.
		boolean eanChanged = !currentProduct.ean13().equals(command.ean13());
		boolean batchChanged = !currentProduct.batchNumber().equals(command.batchNumber());

		if (eanChanged || batchChanged) {
			checkDuplicity(command.ean13(), command.batchNumber());
		}

		// Construye la nueva versión del producto (Record inmutable)
		// Mantenemos el ID original y el Estado original.
		Product updatedProduct = new Product(
				currentProduct.id(),
				command.ean13(),
				command.name(),
				command.batchNumber(),
				command.expiryDate(),
				command.quantity(),
				command.category(),
				currentProduct.status());

		// Persistimos
		return productRepository.save(updatedProduct);
	}
}