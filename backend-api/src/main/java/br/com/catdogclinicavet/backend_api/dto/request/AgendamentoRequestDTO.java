package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AgendamentoRequestDTO(
        @NotNull @Future LocalDateTime dataHora,
        String descricao,
        @NotNull Long animalId,
        Long funcionarioId
) {}