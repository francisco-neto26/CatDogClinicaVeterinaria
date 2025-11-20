package br.com.catdogclinicavet.backend_api.dto.response;

public record UsuarioResponseDTO(
        Long id,
        String email,
        String fotoUrl,
        RoleResponseDTO role,
        PessoaResponseDTO pessoa
) {}
