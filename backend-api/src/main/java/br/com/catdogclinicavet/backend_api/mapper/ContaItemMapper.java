package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.response.ContaItemResponseDTO;
import br.com.catdogclinicavet.backend_api.models.ContaItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ContaItemMapper {

    ContaItemMapper INSTANCE = Mappers.getMapper(ContaItemMapper.class);

    @Mapping(source = "itemServico.id", target = "itemServicoId")
    @Mapping(source = "itemServico.descricao", target = "itemServicoDescricao")
    ContaItemResponseDTO toResponseDTO(ContaItem contaItem);
}