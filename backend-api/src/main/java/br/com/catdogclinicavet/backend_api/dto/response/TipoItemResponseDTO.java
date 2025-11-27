package br.com.catdogclinicavet.backend_api.dto.response;

import java.io.Serializable;

public record TipoItemResponseDTO(
        Long id,
        String nome
) implements Serializable {}
