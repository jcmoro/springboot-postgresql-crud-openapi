package com.example.spring_boot_postgresql_crud.application.service;

import com.example.spring_boot_postgresql_crud.domain.model.Product;

import java.util.List;

/**
 * Inbound port (use case) for product operations. Implementations live in this
 * package; the controller (web adapter) depends on this interface, never on the
 * implementation.
 */
public interface ProductService {

    List<Product> getAll();

    Product getById(Long id);

    Product create(String name, String description, double price);

    Product update(Long id, String name, String description, double price);

    void delete(Long id);
}
