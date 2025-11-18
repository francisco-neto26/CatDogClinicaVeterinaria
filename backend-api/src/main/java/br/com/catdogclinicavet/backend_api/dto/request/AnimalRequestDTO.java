package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record AnimalRequestDTO(
        @NotBlank String nome,
        String raca,
        @PastOrPresent LocalDate dataNascimento,
        @NotBlank String especie,
        @NotBlank String sexo,
        String corPelagem
) {}