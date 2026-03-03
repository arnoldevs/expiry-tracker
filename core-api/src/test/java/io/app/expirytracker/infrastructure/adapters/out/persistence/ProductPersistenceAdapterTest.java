package io.app.expirytracker.infrastructure.adapters.out.persistence;

import io.app.expirytracker.domain.model.Product;
import io.app.expirytracker.domain.model.ProductStatus;
import io.app.expirytracker.infrastructure.adapters.out.persistence.mappers.ProductMapper;
import io.app.expirytracker.infrastructure.adapters.out.persistence.repositories.JpaProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static io.app.expirytracker.domain.model.builders.ProductBuilder.aProduct;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({ ProductPersistenceAdapter.class, ProductMapper.class })
class ProductPersistenceAdapterTest {

    @Autowired
    private ProductPersistenceAdapter productPersistenceAdapter;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Test
    @DisplayName("Debería guardar un producto y recuperarlo por su ID")
    void shouldSaveAndFindProductById() {
        // Arrange
        var productToSave = aProduct()
                .withEan13("8410065000100")
                .withName("Agua Mineral 1L")
                .build();

        // Act
        productPersistenceAdapter.save(productToSave);
        Optional<Product> foundProductOpt = productPersistenceAdapter.findById(productToSave.id());

        // Assert
        assertThat(foundProductOpt).isPresent();
        var foundProduct = foundProductOpt.get();

        assertThat(foundProduct).usingRecursiveComparison().isEqualTo(productToSave);
    }

    @Test
    @DisplayName("Debería realizar un soft-delete cambiando el estado a DISCARDED")
    void shouldSoftDeleteProductByChangingStatus() {
        // Arrange
        var productToSave = aProduct().build();
        productPersistenceAdapter.save(productToSave);
        UUID productId = productToSave.id();

        // Act
        productPersistenceAdapter.deleteById(productId);

        // Assert
        // 1. El adaptador ya no debería encontrar el producto.
        Optional<Product> foundAfterDeleteOpt = productPersistenceAdapter.findById(productId);
        assertThat(foundAfterDeleteOpt).isNotPresent();

        // 2. Verificar directamente en el repositorio que la entidad todavía existe.
        var entityAfterDeleteOpt = jpaProductRepository.findById(productId);
        assertThat(entityAfterDeleteOpt).isPresent();

        // 3. Verificar que el estado de la entidad es DISCARDED.
        assertThat(entityAfterDeleteOpt.get().getStatus()).isEqualTo(ProductStatus.DISCARDED);
    }

    @Test
    @DisplayName("findById no debería devolver productos que no están activos")
    void findById_shouldNotReturnInactiveProducts() {
        // Arrange
        var discardedProduct = aProduct().withStatus(ProductStatus.DISCARDED).build();
        productPersistenceAdapter.save(discardedProduct);

        // Act
        Optional<Product> foundProductOpt = productPersistenceAdapter.findById(discardedProduct.id());

        // Assert
        assertThat(foundProductOpt).isNotPresent();
    }
}
