package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.auth.RegisterRequestDTO;
import br.com.catdogclinicavet.backend_api.models.Pessoa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Pessoa toPessoa(RegisterRequestDTO dto);
}