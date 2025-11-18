package br.com.catdogclinicavet.backend_api.dto.response;

import java.time.LocalDate;

public record AnimalResponseDTO(
        Long id,
        String nome,
        String raca,
        LocalDate dataNascimento,
        String especie,
        String sexo,
        String corPelagem,
        String fotoUrl,
        Long usuarioId
) {}