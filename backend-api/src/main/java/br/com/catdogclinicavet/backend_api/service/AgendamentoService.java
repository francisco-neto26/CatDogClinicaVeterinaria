package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.AgendamentoRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.AgendamentoResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.BusinessLogicException;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.AgendamentoMapper;
import br.com.catdogclinicavet.backend_api.models.Agendamento;
import br.com.catdogclinicavet.backend_api.models.Animal;
import br.com.catdogclinicavet.backend_api.models.Usuario;
import br.com.catdogclinicavet.backend_api.models.enums.AgendamentoStatus;
import br.com.catdogclinicavet.backend_api.repositories.AgendamentoRepository;
import br.com.catdogclinicavet.backend_api.repositories.AnimalRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import br.com.catdogclinicavet.backend_api.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendamentoMapper agendamentoMapper;

    private UserDetailsImpl getAuthenticatedUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public AgendamentoResponseDTO create(AgendamentoRequestDTO dto) {
        UserDetailsImpl userDetails = getAuthenticatedUser();

        Usuario cliente = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        Animal animal = animalRepository.findById(dto.animalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado."));

        if (!Objects.equals(animal.getUsuario().getId(), cliente.getId())) {
            throw new BusinessLogicException("O animal informado não pertence a este usuário.");
        }

        Agendamento agendamento = agendamentoMapper.toEntity(dto);
        agendamento.setCliente(cliente);
        agendamento.setAnimal(animal);
        agendamento.setStatus(AgendamentoStatus.AGENDADO);

        Agendamento saved = agendamentoRepository.save(agendamento);
        return agendamentoMapper.toResponseDTO(saved);
    }

    public Page<AgendamentoResponseDTO> listMine(Pageable pageable) {
        Long userId = getAuthenticatedUser().getId();
        return agendamentoRepository.findByClienteId(userId, pageable)
                .map(agendamentoMapper::toResponseDTO);
    }

    public Page<AgendamentoResponseDTO> listAll(Pageable pageable) {
        return agendamentoRepository.findAll(pageable)
                .map(agendamentoMapper::toResponseDTO);
    }

    public AgendamentoResponseDTO findById(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));

        UserDetailsImpl userDetails = getAuthenticatedUser();
        boolean isFuncionario = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("FUNCIONARIO") || a.getAuthority().equals("MEDICO VETERINARIO"));

        if (!isFuncionario && !Objects.equals(agendamento.getCliente().getId(), userDetails.getId())) {
            throw new BusinessLogicException("Acesso negado.");
        }

        return agendamentoMapper.toResponseDTO(agendamento);
    }

    @Transactional
    public void cancel(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));

        UserDetailsImpl userDetails = getAuthenticatedUser();

        if (!Objects.equals(agendamento.getCliente().getId(), userDetails.getId())) {
            throw new BusinessLogicException("Acesso negado. Apenas o dono do agendamento pode cancelá-lo.");
        }

        if (agendamento.getStatus() != AgendamentoStatus.AGENDADO) {
            throw new BusinessLogicException("Não é possível cancelar um agendamento que já foi concluído ou cancelado.");
        }

        agendamento.setStatus(AgendamentoStatus.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    @Transactional
    public AgendamentoResponseDTO assignVeterinarian(Long agendamentoId, Long funcionarioId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));

        Usuario veterinario = usuarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado."));

        agendamento.setFuncionario(veterinario);
        return agendamentoMapper.toResponseDTO(agendamentoRepository.save(agendamento));
    }

    @Transactional
    public AgendamentoResponseDTO complete(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));

        agendamento.setStatus(AgendamentoStatus.CONCLUIDO);
        return agendamentoMapper.toResponseDTO(agendamentoRepository.save(agendamento));
    }
}