package com.example.spring_boot_postgresql_crud.infrastructure.persistence;

import com.example.spring_boot_postgresql_crud.domain.model.Product;

final class ProductPersistenceMapper {

    private ProductPersistenceMapper() {
    }

    static Product toDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice()
        );
    }

    static ProductJpaEntity toNewJpa(Product product) {
        return new ProductJpaEntity(product.getName(), product.getDescription(), product.getPrice());
    }

    /** Apply the domain product's mutable attributes onto an existing managed JPA entity. */
    static void applyTo(Product product, ProductJpaEntity entity) {
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
    }
}
