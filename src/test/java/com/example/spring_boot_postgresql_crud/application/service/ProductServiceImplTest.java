package com.example.spring_boot_postgresql_crud.application.service;

import com.example.spring_boot_postgresql_crud.domain.exception.ResourceNotFoundException;
import com.example.spring_boot_postgresql_crud.domain.model.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceImplTest {

    private InMemoryProductRepository repo;
    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryProductRepository();
        service = new ProductServiceImpl(repo);
    }

    @Test
    void create_assignsIdAndPersists() {
        Product created = service.create("Laptop", "Dell XPS", 1500.0);

        assertThat(created.getId()).isNotNull();
        assertThat(service.getById(created.getId()).getName()).isEqualTo("Laptop");
    }

    @Test
    void getById_missingId_throws() {
        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void update_mutatesAndReturns() {
        Product created = service.create("Laptop", "Dell XPS", 1500.0);

        Product updated = service.update(created.getId(), "Laptop 2026", "Refresh", 1650.0);

        assertThat(updated.getName()).isEqualTo("Laptop 2026");
        assertThat(updated.getPrice()).isEqualTo(1650.0);
        assertThat(service.getById(created.getId()).getName()).isEqualTo("Laptop 2026");
    }

    @Test
    void update_missingId_throws() {
        assertThatThrownBy(() -> service.update(999L, "x", null, 1.0))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_missingId_throws() {
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existing_removes() {
        Product created = service.create("X", null, 1.0);

        service.delete(created.getId());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getAll_returnsCreatedProducts() {
        service.create("A", null, 1.0);
        service.create("B", null, 2.0);

        assertThat(service.getAll()).hasSize(2);
    }
}
