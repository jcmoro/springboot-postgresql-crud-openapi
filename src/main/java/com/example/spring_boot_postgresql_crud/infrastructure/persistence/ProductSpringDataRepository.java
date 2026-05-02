package com.example.spring_boot_postgresql_crud.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA interface, used internally by {@link ProductPersistenceAdapter}.
 * Package-private: nothing outside this adapter package should reach for it.
 */
interface ProductSpringDataRepository extends JpaRepository<ProductJpaEntity, Long> {
}
