package br.com.catdogclinicavet.backend_api.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorDTO(
        LocalDateTime timestamp,
        Integer status,
        String error,
        Map<String, String> errors,
        String path
) {}
