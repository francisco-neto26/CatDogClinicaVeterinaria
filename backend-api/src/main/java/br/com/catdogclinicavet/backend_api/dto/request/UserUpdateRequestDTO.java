package br.com.catdogclinicavet.backend_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(
        @NotBlank String nome,
        String telefone,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        @Size(max = 2) String uf,
        @Size(max = 10) String cep
) {}