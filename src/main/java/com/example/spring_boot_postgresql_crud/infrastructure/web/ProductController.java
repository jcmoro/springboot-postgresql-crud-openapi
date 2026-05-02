package com.example.spring_boot_postgresql_crud.infrastructure.web;

import com.example.spring_boot_postgresql_crud.application.service.ProductService;
import com.example.spring_boot_postgresql_crud.domain.model.Product;
import com.example.spring_boot_postgresql_crud.infrastructure.web.dto.ProductCreateDTO;
import com.example.spring_boot_postgresql_crud.infrastructure.web.dto.ProductDTO;
import com.example.spring_boot_postgresql_crud.infrastructure.web.dto.ProductUpdateDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class ProductController implements ProductsApi {

    private final ProductService service;

    ProductController(ProductService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<ProductDTO>> listProducts() {
        List<ProductDTO> body = service.getAll().stream()
                .map(ProductWebMapper::toDTO)
                .toList();
        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ProductDTO> getProduct(Long id) {
        return ResponseEntity.ok(ProductWebMapper.toDTO(service.getById(id)));
    }

    @Override
    public ResponseEntity<ProductDTO> createProduct(ProductCreateDTO dto) {
        Product created = service.create(dto.name(), dto.description(), dto.price());
        URI location = URI.create("/api/products/" + created.getId());
        return ResponseEntity.created(location).body(ProductWebMapper.toDTO(created));
    }

    @Override
    public ResponseEntity<ProductDTO> updateProduct(Long id, ProductUpdateDTO dto) {
        Product updated = service.update(id, dto.name(), dto.description(), dto.price());
        return ResponseEntity.ok(ProductWebMapper.toDTO(updated));
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
