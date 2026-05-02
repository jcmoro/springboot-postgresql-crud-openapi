package com.example.spring_boot_postgresql_crud.infrastructure.persistence;

import com.example.spring_boot_postgresql_crud.domain.exception.ResourceNotFoundException;
import com.example.spring_boot_postgresql_crud.domain.model.Product;
import com.example.spring_boot_postgresql_crud.domain.port.ProductRepository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductSpringDataRepository jpa;

    ProductPersistenceAdapter(ProductSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Product> findAll() {
        return jpa.findAll().stream()
                .map(ProductPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpa.findById(id).map(ProductPersistenceMapper::toDomain);
    }

    @Override
    public Product create(Product product) {
        ProductJpaEntity saved = jpa.save(ProductPersistenceMapper.toNewJpa(product));
        return ProductPersistenceMapper.toDomain(saved);
    }

    @Override
    public Product update(Product product) {
        ProductJpaEntity managed = jpa.findById(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", product.getId()));
        ProductPersistenceMapper.applyTo(product, managed);
        // Hibernate dirty-checking flushes on transaction commit.
        return ProductPersistenceMapper.toDomain(managed);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
