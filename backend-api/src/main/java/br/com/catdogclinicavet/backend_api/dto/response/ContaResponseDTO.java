package br.com.catdogclinicavet.backend_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ContaResponseDTO(
        Long id,
        LocalDateTime dataEmissao,
        BigDecimal valorTotal,
        String status,
        Long clienteId,
        Long agendamentoId,
        List<ContaItemResponseDTO> itens,
        List<TituloReceberResponseDTO> titulos
) {}