package io.app.expirytracker.domain.model.builders;

import io.app.expirytracker.domain.model.Product;
import io.app.expirytracker.domain.model.ProductStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Implementa el patrón Test Data Builder para el modelo de dominio Product.
 *
 * Este builder proporciona una API fluida para construir objetos Product para
 * fines de prueba. Encapsula la lógica para crear instancias válidas por
 * defecto y permite que las pruebas sobrescriban solo los campos relevantes
 * para sus escenarios específicos.
 */
public class ProductBuilder {

    private UUID id = UUID.randomUUID();
    private String ean13 = "1234567890123";
    private String name = "Default Product Name";
    private String batchNumber = "DEFAULT-BATCH-001";
    private LocalDate expiryDate = LocalDate.now().plusYears(1);
    private int quantity = 10;
    private String category = "Default Category";
    private ProductStatus status = ProductStatus.ACTIVE;

    /**
     * Constructor privado para forzar el uso del método de fábrica estático.
     */
    private ProductBuilder() {
    }

    /**
     * Método de fábrica estático para proporcionar un punto de entrada claro para crear un builder.
     *
     * @return una nueva instancia de ProductBuilder con valores por defecto.
     */
    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withEan13(String ean13) {
        this.ean13 = ean13;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public ProductBuilder withExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public ProductBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public ProductBuilder withStatus(ProductStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Construye la instancia final de Product a partir del estado actual del builder.
     *
     * @return un objeto inmutable Product.
     */
    public Product build() {
        return new Product(
                id,
                ean13,
                name,
                batchNumber,
                expiryDate,
                quantity,
                category,
                status);
    }
}

