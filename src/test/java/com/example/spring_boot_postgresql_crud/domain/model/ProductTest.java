package com.example.spring_boot_postgresql_crud.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void newProduct_withNullId_holdsAttributes() {
        Product p = new Product("Laptop", "Dell XPS", 1500.0);

        assertThat(p.getId()).isNull();
        assertThat(p.getName()).isEqualTo("Laptop");
        assertThat(p.getDescription()).isEqualTo("Dell XPS");
        assertThat(p.getPrice()).isEqualTo(1500.0);
    }

    @Test
    void reconstitute_keepsId() {
        Product p = new Product(42L, "Mouse", null, 30.0);

        assertThat(p.getId()).isEqualTo(42L);
    }

    @Test
    void blankName_isRejected() {
        assertThatThrownBy(() -> new Product("  ", "x", 10.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void negativePrice_isRejected() {
        assertThatThrownBy(() -> new Product("Laptop", null, -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
    }

    @Test
    void changeAttributes_validatesAndMutates() {
        Product p = new Product("Old", null, 10.0);

        p.changeAttributes("New", "desc", 20.0);

        assertThat(p.getName()).isEqualTo("New");
        assertThat(p.getDescription()).isEqualTo("desc");
        assertThat(p.getPrice()).isEqualTo(20.0);
    }

    @Test
    void changeAttributes_rejectsInvalidPrice() {
        Product p = new Product("X", null, 10.0);

        assertThatThrownBy(() -> p.changeAttributes("X", null, -5.0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
