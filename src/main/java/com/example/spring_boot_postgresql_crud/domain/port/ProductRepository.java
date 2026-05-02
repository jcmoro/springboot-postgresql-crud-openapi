package com.example.spring_boot_postgresql_crud.domain.port;

import com.example.spring_boot_postgresql_crud.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for product persistence. The domain and application layers
 * depend on this interface; the actual implementation is an infrastructure
 * adapter that may use JPA, JDBC, an in-memory store, or anything else.
 */
public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    /** Insert a new product. Caller must pass a Product with id == null. */
    Product create(Product product);

    /** Update the persisted state of a product that already exists. */
    Product update(Product product);

    boolean existsById(Long id);

    void deleteById(Long id);
}
