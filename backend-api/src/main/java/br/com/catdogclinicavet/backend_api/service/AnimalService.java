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
import br.com.catdogclinicavet.backend_api.security.AppRoles;
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
    // Limpa o cache de ID individual ao criar um novo, para garantir consistência se necessário
    @CacheEvict(value = "animal", allEntries = true)
    public AnimalResponseDTO createAnimal(AnimalRequestDTO dto, MultipartFile foto) {
        UserDetailsImpl userDetails = getAuthenticatedUser();

        boolean isAdminOrVet = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_" + AppRoles.FUNCIONARIO) ||
                                role.equals("ROLE_" + AppRoles.VETERINARIO) ||
                                role.equals("ROLE_" + AppRoles.ADMIN)
                );

        Long donoId;

        if (isAdminOrVet && dto.clienteId() != null) {
            donoId = dto.clienteId();
        } else {
            donoId = userDetails.getId();
        }

        Usuario dono = usuarioRepository.findById(donoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dono do animal não encontrado (ID: " + donoId + ")"));

        Animal animal = animalMapper.toEntity(dto);
        animal.setUsuario(dono);

        if (foto != null && !foto.isEmpty()) {
            String fotoUrl = storageService.uploadFile(foto);
            animal.setFotoUrl(fotoUrl);
        }

        Animal savedAnimal = animalRepository.save(animal);
        return animalMapper.toResponseDTO(savedAnimal);
    }

    public Page<AnimalResponseDTO> findAllAnimals(Pageable pageable) {
        UserDetailsImpl userDetails = getAuthenticatedUser();

        boolean isAdminOrVet = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_" + AppRoles.FUNCIONARIO) ||
                                role.equals("ROLE_" + AppRoles.VETERINARIO) ||
                                role.equals("ROLE_" + AppRoles.ADMIN)
                );

        if (isAdminOrVet) {
            return animalRepository.findAll(pageable)
                    .map(animalMapper::toResponseDTO);
        } else {
            return animalRepository.findByUsuarioId(userDetails.getId(), pageable)
                    .map(animalMapper::toResponseDTO);
        }
    }

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
    @CacheEvict(value = "animal", key = "#id") // Limpa apenas o cache deste animal específico
    public AnimalResponseDTO updateAnimal(Long id, AnimalRequestDTO dto, MultipartFile foto) {
        UserDetailsImpl userDetails = getAuthenticatedUser(); // Usar método seguro

        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com ID: " + id));

        boolean isAdminOrVet = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_" + AppRoles.FUNCIONARIO) ||
                                role.equals("ROLE_" + AppRoles.VETERINARIO) ||
                                role.equals("ROLE_" + AppRoles.ADMIN)
                );

        // Validação de segurança
        if (!isAdminOrVet && !Objects.equals(animal.getUsuario().getId(), userDetails.getId())) {
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para atualizar este animal.");
        }

        animalMapper.updateEntityFromDto(dto, animal);

        if (foto != null && !foto.isEmpty()) {
            // Limpa foto antiga se existir
            if (animal.getFotoUrl() != null && !animal.getFotoUrl().isBlank()) {
                try {
                    String oldUrl = animal.getFotoUrl();
                    String oldFileName = oldUrl.substring(oldUrl.lastIndexOf("/") + 1);
                    storageService.deleteFile(oldFileName);
                } catch (Exception e) {
                    // Log silencioso
                }
            }

            String fotoUrl = storageService.uploadFile(foto);
            animal.setFotoUrl(fotoUrl);
        }

        Animal updatedAnimal = animalRepository.save(animal);
        return animalMapper.toResponseDTO(updatedAnimal);
    }

    @Transactional
    @CacheEvict(value = "animal", key = "#id")
    public void deleteAnimal(Long id) {
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
            throw new BusinessLogicException("Acesso negado: Você não tem permissão para deletar este animal.");
        }
        animalRepository.delete(animal);
    }
}