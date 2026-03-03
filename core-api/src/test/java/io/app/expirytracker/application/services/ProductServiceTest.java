package io.app.expirytracker.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.app.expirytracker.application.ports.out.ProductRepositoryPort;
import io.app.expirytracker.domain.model.Product;
import io.app.expirytracker.domain.model.ProductStatus;
import io.app.expirytracker.domain.model.builders.CreateProductCommandBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test unitario para el servicio de aplicación ProductService.
 *
 * Se enfoca en probar la lógica de orquestación y las reglas de negocio
 * dentro del servicio, aislando la capa de persistencia a través de mocks.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    @DisplayName("Debería crear un producto cuando el comando es válido y no hay duplicados")
    void shouldCreateProduct_whenCommandIsValidAndNoDuplicates() {
        // Arrange
        final var command = CreateProductCommandBuilder.aCreateProductCommand().build();

        when(productRepository.existsByEan13AndBatchNumber(command.ean13(), command.batchNumber())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            final var product = (Product) invocation.getArgument(0);
            return new Product(
                    java.util.UUID.randomUUID(),
                    product.ean13(),
                    product.name(),
                    product.batchNumber(),
                    product.expiryDate(),
                    product.quantity(),
                    product.category(),
                    product.status());
        });

        // Act
        final var createdProduct = productService.create(command);

        // Assert
        verify(productRepository).save(productCaptor.capture());
        final var capturedProduct = productCaptor.getValue();

        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.id()).isNotNull();

        assertThat(capturedProduct.status()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(capturedProduct)
                .usingRecursiveComparison()
                .ignoringFields("id", "status")
                .isEqualTo(command);
    }

    @Test
    @DisplayName("Debería lanzar una excepción al intentar crear un producto duplicado")
    void shouldThrowException_whenCreatingDuplicateProduct() {
        // Arrange
        final var command = CreateProductCommandBuilder.aCreateProductCommand().build();
        when(productRepository.existsByEan13AndBatchNumber(command.ean13(), command.batchNumber())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productService.create(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("Ya existe un registro para el producto [%s] con el lote [%s].",
                        command.ean13(), command.batchNumber()));

        // Verifica que el método 'save' del repositorio NUNCA fue llamado.
        verify(productRepository, never()).save(any(Product.class));
    }
}
