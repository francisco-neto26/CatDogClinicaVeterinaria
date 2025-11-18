package br.com.catdogclinicavet.backend_api.dto.response;

public record PessoaResponseDTO(
        Long id,
        String nome,
        String telefone,
        String cpfcnpj,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String uf,
        String cep
) {}