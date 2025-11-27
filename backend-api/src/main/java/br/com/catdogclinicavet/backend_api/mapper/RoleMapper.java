package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.request.RoleRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.RoleResponseDTO;
import br.com.catdogclinicavet.backend_api.models.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleResponseDTO toResponseDTO(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Role toEntity(RoleRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    void updateEntityFromDto(RoleRequestDTO dto, @MappingTarget Role role);
}