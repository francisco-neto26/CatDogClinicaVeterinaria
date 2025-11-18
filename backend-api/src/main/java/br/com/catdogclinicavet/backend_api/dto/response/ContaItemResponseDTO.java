package br.com.catdogclinicavet.backend_api.dto.response;

import java.math.BigDecimal;

public record ContaItemResponseDTO(
        Long id,
        Long itemServicoId,
        String itemServicoDescricao,
        Integer quantidade,
        BigDecimal precoUnitarioMomento,
        BigDecimal subtotal
) {}