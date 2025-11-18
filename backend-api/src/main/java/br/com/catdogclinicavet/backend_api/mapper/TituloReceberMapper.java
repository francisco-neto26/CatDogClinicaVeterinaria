package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.request.TituloReceberRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.TituloReceberResponseDTO;
import br.com.catdogclinicavet.backend_api.models.TituloReceber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TituloReceberMapper {

    TituloReceberMapper INSTANCE = Mappers.getMapper(TituloReceberMapper.class);

    @Mapping(source = "conta.id", target = "contaId")
    @Mapping(source = "cliente.id", target = "clienteId")
    TituloReceberResponseDTO toResponseDTO(TituloReceber tituloReceber);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataEmissao", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "conta", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    TituloReceber toEntity(TituloReceberRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataEmissao", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "conta", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    void updateEntityFromDto(TituloReceberRequestDTO dto, @MappingTarget TituloReceber tituloReceber);
}