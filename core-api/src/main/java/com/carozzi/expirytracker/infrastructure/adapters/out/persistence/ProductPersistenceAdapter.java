package com.carozzi.expirytracker.infrastructure.adapters.out.persistence;

import com.carozzi.expirytracker.application.ports.out.ProductRepositoryPort;
import com.carozzi.expirytracker.domain.model.Product;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.mappers.ProductMapper;
import com.carozzi.expirytracker.infrastructure.adapters.out.persistence.repositories.JpaProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

	private final JpaProductRepository jpaProductRepository;
	private final ProductMapper productMapper;

	@Override
	public Product save(Product product) {
		var entity = productMapper.toEntity(product);
		var savedEntity = jpaProductRepository.save(entity);
		return productMapper.toDomain(savedEntity);
	}

	@Override
	public Optional<Product> findById(UUID id) {
		return jpaProductRepository.findById(id)
				.map(productMapper::toDomain);
	}

	@Override
	public Optional<Product> findByEan13(String ean13) {
		return jpaProductRepository.findByEan13(ean13)
				.map(productMapper::toDomain);
	}

	@Override
	public boolean existsByEan13AndBatchNumber(String ean13, String batchNumber) {
		return jpaProductRepository.existsByEan13AndBatchNumber(ean13, batchNumber);
	}

	@Override
	public List<Product> findAll() {
		return jpaProductRepository.findAll()
				.stream()
				.map(productMapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteById(UUID id) {
		jpaProductRepository.deleteById(id);
	}
}