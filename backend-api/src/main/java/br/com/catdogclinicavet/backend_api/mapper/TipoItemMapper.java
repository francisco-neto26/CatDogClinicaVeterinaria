package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.request.TipoItemRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.TipoItemResponseDTO;
import br.com.catdogclinicavet.backend_api.models.TipoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TipoItemMapper {

    TipoItemMapper INSTANCE = Mappers.getMapper(TipoItemMapper.class);

    TipoItemResponseDTO toResponseDTO(TipoItem tipoItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itens", ignore = true)
    TipoItem toEntity(TipoItemRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itens", ignore = true)
    void updateEntityFromDto(TipoItemRequestDTO dto, @MappingTarget TipoItem tipoItem);
}