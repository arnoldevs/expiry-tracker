package io.app.expirytracker.domain.model.builders;

import java.time.LocalDate;

import io.app.expirytracker.application.ports.in.CreateProductUseCase.CreateProductCommand;

/**
 * Implementa el patrón Test Data Builder para el comando CreateProductCommand.
 *
 * Proporciona una API fluida para construir objetos CreateProductCommand para
 * pruebas, con valores por defecto razonables.
 */
public class CreateProductCommandBuilder {

    private String ean13 = "9876543210987";
    private String name = "Default CommandProduct Name";
    private String batchNumber = "DEFAULT-COMM-BATCH-001";
    private LocalDate expiryDate = LocalDate.now().plusMonths(6);
    private int quantity = 20;
    private String category = "Default Command Category";

    private CreateProductCommandBuilder() {
    }

    public static CreateProductCommandBuilder aCreateProductCommand() {
        return new CreateProductCommandBuilder();
    }

    public CreateProductCommandBuilder withEan13(String ean13) {
        this.ean13 = ean13;
        return this;
    }

    public CreateProductCommandBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CreateProductCommandBuilder withBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public CreateProductCommandBuilder withExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public CreateProductCommandBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public CreateProductCommandBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public CreateProductCommand build() {
        return new CreateProductCommand(
                ean13,
                name,
                batchNumber,
                expiryDate,
                quantity,
                category);
    }
}
