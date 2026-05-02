package com.example.spring_boot_postgresql_crud.domain.model;

/**
 * Domain entity. Pure POJO with invariants enforced in the constructor and
 * mutations confined to {@link #changeAttributes(String, String, double)}.
 * No Spring, no JPA, no Jackson — this class compiles in plain Java.
 */
public final class Product {

    private final Long id;
    private String name;
    private String description;
    private double price;

    /** Construct a new product not yet persisted (id is null until the adapter saves it). */
    public Product(String name, String description, double price) {
        this(null, name, description, price);
    }

    /** Reconstitute a product from persistence. */
    public Product(Long id, String name, String description, double price) {
        validate(name, price);
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    /** Apply an update. The product keeps its id. */
    public void changeAttributes(String name, String description, double price) {
        validate(name, price);
        this.name = name;
        this.description = description;
        this.price = price;
    }

    private static void validate(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Product price must be >= 0");
        }
    }
}
