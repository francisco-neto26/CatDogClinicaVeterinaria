package br.com.catdogclinicavet.backend_api.exceptions;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path
) {}