package com.example.spring_boot_postgresql_crud.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCreateDTO(
        @NotBlank @Size(min = 1, max = 255) String name,
        @Size(max = 1000) String description,
        @NotNull @DecimalMin("0") Double price
) {
}
