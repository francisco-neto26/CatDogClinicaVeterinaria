package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.request.PessoaRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.PessoaResponseDTO;
import br.com.catdogclinicavet.backend_api.models.Pessoa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PessoaMapper {

    PessoaMapper INSTANCE = Mappers.getMapper(PessoaMapper.class);

    PessoaResponseDTO toResponseDTO(Pessoa pessoa);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Pessoa toEntity(PessoaRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    void updateEntityFromDto(PessoaRequestDTO dto, @MappingTarget Pessoa pessoa);
}
