package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.request.ItemServicoRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.ItemServicoResponseDTO;
import br.com.catdogclinicavet.backend_api.models.ItemServico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ItemServicoMapper {

    ItemServicoMapper INSTANCE = Mappers.getMapper(ItemServicoMapper.class);

    @Mapping(source = "tipoItem.id", target = "tipoItemId")
    ItemServicoResponseDTO toResponseDTO(ItemServico itemServico);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoItem", ignore = true)
    ItemServico toEntity(ItemServicoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoItem", ignore = true)
    void updateEntityFromDto(ItemServicoRequestDTO dto, @MappingTarget ItemServico itemServico);
}