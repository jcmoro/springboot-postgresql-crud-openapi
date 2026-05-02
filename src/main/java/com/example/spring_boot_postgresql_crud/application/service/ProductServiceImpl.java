package com.example.spring_boot_postgresql_crud.application.service;

import com.example.spring_boot_postgresql_crud.domain.exception.ResourceNotFoundException;
import com.example.spring_boot_postgresql_crud.domain.model.Product;
import com.example.spring_boot_postgresql_crud.domain.port.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> getAll() {
        return repository.findAll();
    }

    @Override
    public Product getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Override
    @Transactional
    public Product create(String name, String description, double price) {
        return repository.create(new Product(name, description, price));
    }

    @Override
    @Transactional
    public Product update(Long id, String name, String description, double price) {
        Product product = getById(id);
        product.changeAttributes(name, description, price);
        return repository.update(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        repository.deleteById(id);
    }
}
