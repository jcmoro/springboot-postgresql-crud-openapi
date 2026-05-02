package com.example.spring_boot_postgresql_crud.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record FieldErrorDTO(
        @NotNull String field,
        @NotNull String message
) {
}
