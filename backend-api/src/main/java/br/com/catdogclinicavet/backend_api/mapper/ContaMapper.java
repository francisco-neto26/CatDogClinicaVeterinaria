package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.response.ContaResponseDTO;
import br.com.catdogclinicavet.backend_api.models.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ContaItemMapper.class, TituloReceberMapper.class})
public interface ContaMapper {

    ContaMapper INSTANCE = Mappers.getMapper(ContaMapper.class);

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "agendamento.id", target = "agendamentoId")
    @Mapping(source = "itens", target = "itens")
    @Mapping(source = "titulos", target = "titulos")
    ContaResponseDTO toResponseDTO(Conta conta);
}