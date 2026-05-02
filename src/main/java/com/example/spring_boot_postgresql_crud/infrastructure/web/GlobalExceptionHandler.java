package com.example.spring_boot_postgresql_crud.infrastructure.web;

import com.example.spring_boot_postgresql_crud.domain.exception.ResourceNotFoundException;
import com.example.spring_boot_postgresql_crud.infrastructure.web.dto.FieldErrorDTO;
import com.example.spring_boot_postgresql_crud.infrastructure.web.dto.ProblemDTO;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

/**
 * Translates exceptions raised in the application/domain layers into
 * RFC 9457 application/problem+json responses described by {@link ProblemDTO}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_BASE = "https://api.example.com/problems/";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        String typeSlug = ex.getResourceType().toLowerCase() + "-not-found";
        String code = ex.getResourceType().toUpperCase() + "_NOT_FOUND";
        ProblemDTO body = new ProblemDTO(
                URI.create(PROBLEM_BASE + typeSlug),
                ex.getResourceType() + " not found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                URI.create(req.getRequestURI()),
                code,
                null
        );
        return problemResponse(HttpStatus.NOT_FOUND, body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDTO> handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldErrorDTO> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldErrorDTO(fe.getField(), fe.getDefaultMessage()))
                .toList();
        ProblemDTO body = new ProblemDTO(
                URI.create(PROBLEM_BASE + "validation-error"),
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                "One or more fields are invalid",
                URI.create(req.getRequestURI()),
                "VALIDATION_ERROR",
                fieldErrors
        );
        return problemResponse(HttpStatus.BAD_REQUEST, body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDTO> handleDomainInvariant(IllegalArgumentException ex, HttpServletRequest req) {
        // Thrown by the domain when an invariant is violated (e.g. blank name, negative price).
        ProblemDTO body = new ProblemDTO(
                URI.create(PROBLEM_BASE + "validation-error"),
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                URI.create(req.getRequestURI()),
                "VALIDATION_ERROR",
                null
        );
        return problemResponse(HttpStatus.BAD_REQUEST, body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDTO> handleAnyOther(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception while processing {} {}", req.getMethod(), req.getRequestURI(), ex);
        ProblemDTO body = new ProblemDTO(
                URI.create(PROBLEM_BASE + "internal-error"),
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                URI.create(req.getRequestURI()),
                "INTERNAL_ERROR",
                null
        );
        return problemResponse(HttpStatus.INTERNAL_SERVER_ERROR, body);
    }

    private static ResponseEntity<ProblemDTO> problemResponse(HttpStatus status, ProblemDTO body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }
}
