package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TipoItemRequestDTO(
        @NotBlank String nome
) {}