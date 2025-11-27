package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TituloReceberRequestDTO(
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @NotNull @FutureOrPresent LocalDate dataVencimento
) {}
