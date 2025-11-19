package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.AnimalRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.AnimalResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.BusinessLogicException;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.AnimalMapper;
import br.com.catdogclinicavet.backend_api.models.Animal;
import br.com.catdogclinicavet.backend_api.models.Usuario;
import br.com.catdogclinicavet.backend_api.repositories.AnimalRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import br.com.catdogclinicavet.backend_api.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private StorageService storageService;

    private Long getAuthenticatedUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @Transactional
    @CacheEvict(value = "animais", allEntries = true)
    public AnimalResponseDTO createAnimal(AnimalRequestDTO dto, MultipartFile foto) {
        Long authenticatedUserId = getAuthenticatedUserId();
        Usuario cliente = usuarioRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        Animal animal = animalMapper.toEntity(dto);
        animal.setUsuario(cliente);

        if (foto != null && !foto.isEmpty()) {
            String fotoUrl = storageService.uploadFile(foto);
            animal.setFotoUrl(fotoUrl);
        }

        Animal savedAnimal = animalRepository.save(animal);
        return animalMapper.toResponseDTO(savedAnimal);
    }

    @Cacheable(value = "animais", key = "'all'")
    public Page<AnimalResponseDTO> findAllAnimals(Pageable pageable) {
        Long authenticatedUserId = getAuthenticatedUserId();
        return animalRepository.findByUsuarioId(authenticatedUserId, pageable)
                .map(animalMapper::toResponseDTO);
    }

    @Cacheable(value = "animal", key = "#id")
    public AnimalResponseDTO findAnimalById(Long id) {
        Long authenticatedUserId = getAuthenticatedUserId();
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com ID: " + id));

        if (!Objects.equals(animal.getUsuario().getId(), authenticatedUserId)) {
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para visualizar este animal.");
        }
        return animalMapper.toResponseDTO(animal);
    }

    @Transactional
    @CacheEvict(value = {"animais", "animal"}, allEntries = true)
    public AnimalResponseDTO updateAnimal(Long id, AnimalRequestDTO dto, MultipartFile foto) {
        Long authenticatedUserId = getAuthenticatedUserId();
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com ID: " + id));

        if (!Objects.equals(animal.getUsuario().getId(), authenticatedUserId)) {
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para atualizar este animal.");
        }

        animalMapper.updateEntityFromDto(dto, animal);

        if (foto != null && !foto.isEmpty()) {
            String fotoUrl = storageService.uploadFile(foto);
            animal.setFotoUrl(fotoUrl);
        } else if (dto.dataNascimento() == null && animal.getFotoUrl() != null) {
            // Se foto for nula e não houver nova foto, mas havia uma antiga, manter a antiga.
            // A menos que a intenção seja remover explicitamente (DTO precisaria de um campo para isso)
            // Por enquanto, só atualiza se uma nova foto for fornecida.
        }

        Animal updatedAnimal = animalRepository.save(animal);
        return animalMapper.toResponseDTO(updatedAnimal);
    }

    @Transactional
    @CacheEvict(value = {"animais", "animal"}, allEntries = true)
    public void deleteAnimal(Long id) {
        Long authenticatedUserId = getAuthenticatedUserId();
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com ID: " + id));

        if (!Objects.equals(animal.getUsuario().getId(), authenticatedUserId)) {
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para deletar este animal.");
        }
        animalRepository.delete(animal);
    }
}
