package br.com.catdogclinicavet.backend_api.dto.response;

import java.math.BigDecimal;

public record ItemServicoResponseDTO(
        Long id,
        String descricao,
        BigDecimal precoUnitario,
        Long tipoItemId
) {}