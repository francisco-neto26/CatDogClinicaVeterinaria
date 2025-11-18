package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ItemServicoRequestDTO(
        @NotBlank String descricao,
        @NotNull @DecimalMin("0.01") BigDecimal precoUnitario,
        @NotNull Long tipoItemId
) {}