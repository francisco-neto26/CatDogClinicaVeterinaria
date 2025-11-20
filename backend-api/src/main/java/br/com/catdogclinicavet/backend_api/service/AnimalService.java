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
import br.com.catdogclinicavet.backend_api.security.AppRoles; // <--- Importante
import br.com.catdogclinicavet.backend_api.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

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

    private UserDetailsImpl getAuthenticatedUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    @CacheEvict(value = "animais", allEntries = true)
    public AnimalResponseDTO createAnimal(AnimalRequestDTO dto, MultipartFile foto) {
        Long authenticatedUserId = getAuthenticatedUser().getId();
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

    @Cacheable(value = "animais", key = "#pageable.pageNumber")
    public Page<AnimalResponseDTO> findAllAnimals(Pageable pageable) {
        UserDetailsImpl userDetails = getAuthenticatedUser();

        // Lógica de verificação robusta usando AppRoles
        boolean isAdminOrVet = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_" + AppRoles.FUNCIONARIO) ||
                                role.equals("ROLE_" + AppRoles.VETERINARIO) ||
                                role.equals("ROLE_" + AppRoles.ADMIN)
                );

        if (isAdminOrVet) {
            // Se é Admin, busca todos
            return animalRepository.findAll(pageable)
                    .map(animalMapper::toResponseDTO);
        } else {
            // Se é Cliente, busca só os dele
            return animalRepository.findByUsuarioId(userDetails.getId(), pageable)
                    .map(animalMapper::toResponseDTO);
        }
    }

    // ... (Mantenha os outros métodos findById, updateAnimal, deleteAnimal iguais) ...
    // Vou colocar aqui apenas o findById para garantir o import correto

    @Cacheable(value = "animal", key = "#id")
    public AnimalResponseDTO findAnimalById(Long id) {
        UserDetailsImpl userDetails = getAuthenticatedUser();
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com ID: " + id));

        boolean isAdminOrVet = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_" + AppRoles.FUNCIONARIO) ||
                                role.equals("ROLE_" + AppRoles.VETERINARIO) ||
                                role.equals("ROLE_" + AppRoles.ADMIN)
                );

        if (!isAdminOrVet && !Objects.equals(animal.getUsuario().getId(), userDetails.getId())) {
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para visualizar este animal.");
        }
        return animalMapper.toResponseDTO(animal);
    }

    @Transactional
    @CacheEvict(value = {"animais", "animal"}, allEntries = true)
    public AnimalResponseDTO updateAnimal(Long id, AnimalRequestDTO dto, MultipartFile foto) {
        Long authenticatedUserId = getAuthenticatedUser().getId();
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com ID: " + id));

        if (!Objects.equals(animal.getUsuario().getId(), authenticatedUserId)) {
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para atualizar este animal.");
        }

        animalMapper.updateEntityFromDto(dto, animal);

        if (foto != null && !foto.isEmpty()) {
            String fotoUrl = storageService.uploadFile(foto);
            animal.setFotoUrl(fotoUrl);
        }

        Animal updatedAnimal = animalRepository.save(animal);
        return animalMapper.toResponseDTO(updatedAnimal);
    }

    @Transactional
    @CacheEvict(value = {"animais", "animal"}, allEntries = true)
    public void deleteAnimal(Long id) {
        Long authenticatedUserId = getAuthenticatedUser().getId();
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com ID: " + id));

        if (!Objects.equals(animal.getUsuario().getId(), authenticatedUserId)) {
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para deletar este animal.");
        }
        animalRepository.delete(animal);
    }
}