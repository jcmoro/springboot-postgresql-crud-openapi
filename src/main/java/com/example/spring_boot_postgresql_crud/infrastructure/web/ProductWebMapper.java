package com.example.spring_boot_postgresql_crud.infrastructure.web;

import com.example.spring_boot_postgresql_crud.domain.model.Product;
import com.example.spring_boot_postgresql_crud.infrastructure.web.dto.ProductDTO;

final class ProductWebMapper {

    private ProductWebMapper() {
    }

    static ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
