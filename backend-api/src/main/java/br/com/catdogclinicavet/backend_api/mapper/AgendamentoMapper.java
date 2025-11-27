package br.com.catdogclinicavet.backend_api.mapper;

import br.com.catdogclinicavet.backend_api.dto.request.AgendamentoRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.AgendamentoResponseDTO;
import br.com.catdogclinicavet.backend_api.models.Agendamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AgendamentoMapper {

    AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "animal.id", target = "animalId")
    @Mapping(source = "funcionario.id", target = "funcionarioId")
    AgendamentoResponseDTO toResponseDTO(Agendamento agendamento);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "animal", ignore = true)
    @Mapping(target = "funcionario", ignore = true)
    @Mapping(target = "conta", ignore = true)
    Agendamento toEntity(AgendamentoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "animal", ignore = true)
    @Mapping(target = "funcionario", ignore = true)
    @Mapping(target = "conta", ignore = true)
    void updateEntityFromDto(AgendamentoRequestDTO dto, @MappingTarget Agendamento agendamento);
}
