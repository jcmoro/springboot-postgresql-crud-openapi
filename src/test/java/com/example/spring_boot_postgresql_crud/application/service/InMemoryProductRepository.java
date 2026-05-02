package com.example.spring_boot_postgresql_crud.application.service;

import com.example.spring_boot_postgresql_crud.domain.exception.ResourceNotFoundException;
import com.example.spring_boot_postgresql_crud.domain.model.Product;
import com.example.spring_boot_postgresql_crud.domain.port.ProductRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory fake of the outbound port. Used by the application-layer unit tests
 * to exercise {@link ProductServiceImpl} without Spring or a database.
 */
class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> store = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Product create(Product product) {
        long id = sequence.incrementAndGet();
        Product saved = new Product(id, product.getName(), product.getDescription(), product.getPrice());
        store.put(id, saved);
        return saved;
    }

    @Override
    public Product update(Product product) {
        if (!store.containsKey(product.getId())) {
            throw new ResourceNotFoundException("Product", product.getId());
        }
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
