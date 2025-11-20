package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.response.AnimalResponseDTO;
import br.com.catdogclinicavet.backend_api.mapper.AnimalMapper;
import br.com.catdogclinicavet.backend_api.models.Animal;
import br.com.catdogclinicavet.backend_api.repositories.AnimalRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import br.com.catdogclinicavet.backend_api.security.AppRoles;
import br.com.catdogclinicavet.backend_api.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @InjectMocks
    private AnimalService animalService;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AnimalMapper animalMapper;

    @Mock
    private StorageService storageService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockLoggedUser(Long userId, String role) {
        reset(securityContext, authentication, userDetails);

        lenient().when(userDetails.getId()).thenReturn(userId);

        // Cria a lista de Authorities com a Role passada
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        lenient().doReturn(authorities).when(userDetails).getAuthorities();

        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("ADMIN deve listar todos os animais")
    void findAllAnimals_WhenAdmin_ShouldReturnAll() {
        mockLoggedUser(1L, "ROLE_" + AppRoles.FUNCIONARIO);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Animal> animalPage = new PageImpl<>(List.of(new Animal()));

        when(animalRepository.findAll(pageable)).thenReturn(animalPage);
        when(animalMapper.toResponseDTO(any())).thenReturn(new AnimalResponseDTO(1L, "Rex", null, null, null, null, null, null, 1L));

        Page<AnimalResponseDTO> result = animalService.findAllAnimals(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(animalRepository, times(1)).findAll(pageable);
        verify(animalRepository, never()).findByUsuarioId(anyLong(), any());
    }

    @Test
    @DisplayName("CLIENTE deve listar apenas seus animais")
    void findAllAnimals_WhenCliente_ShouldReturnMineOnly() {

        Long userId = 50L;
        mockLoggedUser(userId, "ROLE_" + AppRoles.CLIENTE);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Animal> animalPage = new PageImpl<>(List.of(new Animal()));

        when(animalRepository.findByUsuarioId(eq(userId), any(Pageable.class))).thenReturn(animalPage);
        when(animalMapper.toResponseDTO(any())).thenReturn(new AnimalResponseDTO(2L, "Totó", null, null, null, null, null, null, userId));

        Page<AnimalResponseDTO> result = animalService.findAllAnimals(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(animalRepository, times(1)).findByUsuarioId(eq(userId), any(Pageable.class));
        verify(animalRepository, never()).findAll(pageable);
    }
}