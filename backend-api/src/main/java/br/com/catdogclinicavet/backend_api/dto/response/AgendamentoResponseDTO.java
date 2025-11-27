package br.com.catdogclinicavet.backend_api.dto.response;

import java.time.LocalDateTime;

public record AgendamentoResponseDTO(
        Long id,
        LocalDateTime dataHora,
        String descricao,
        String status,
        Long clienteId,
        Long animalId,
        Long funcionarioId
) {}