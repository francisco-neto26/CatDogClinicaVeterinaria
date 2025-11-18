package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.request.AnimalRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.AnimalResponseDTO;
import br.com.catdogclinicavet.backend_api.models.Animal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    AnimalMapper INSTANCE = Mappers.getMapper(AnimalMapper.class);

    @Mapping(source = "usuario.id", target = "usuarioId")
    AnimalResponseDTO toResponseDTO(Animal animal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fotoUrl", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "agendamentos", ignore = true)
    @Mapping(target = "sexo", ignore = true)
    Animal toEntity(AnimalRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fotoUrl", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "agendamentos", ignore = true)
    @Mapping(target = "sexo", ignore = true)
    void updateEntityFromDto(AnimalRequestDTO dto, @MappingTarget Animal animal);
}