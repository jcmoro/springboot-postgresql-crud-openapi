package com.example.spring_boot_postgresql_crud.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.net.URI;
import java.util.List;

public record ProblemDTO(
        URI type,
        @NotNull String title,
        @NotNull @Min(100) @Max(599) Integer status,
        String detail,
        URI instance,
        @NotNull @Pattern(regexp = "^[A-Z][A-Z0-9_]*$") String code,
        @Valid List<FieldErrorDTO> errors
) {
}
