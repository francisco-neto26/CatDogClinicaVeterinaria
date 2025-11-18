package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ContaItemRequestDTO(
        @NotNull Long itemServicoId,
        @NotNull @Min(1) Integer quantidade
) {}