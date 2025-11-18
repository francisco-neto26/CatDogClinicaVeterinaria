package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RoleRequestDTO(
        @NotBlank String nome
) {}