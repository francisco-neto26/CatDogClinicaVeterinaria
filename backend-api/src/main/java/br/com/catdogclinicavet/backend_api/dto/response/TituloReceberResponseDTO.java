package br.com.catdogclinicavet.backend_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TituloReceberResponseDTO(
        Long id,
        BigDecimal valor,
        LocalDate dataEmissao,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        String status,
        Long contaId,
        Long clienteId
) {}
