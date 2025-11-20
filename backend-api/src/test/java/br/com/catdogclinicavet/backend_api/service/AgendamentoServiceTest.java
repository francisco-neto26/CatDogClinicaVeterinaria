package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.AgendamentoRequestDTO;
import br.com.catdogclinicavet.backend_api.exceptions.BusinessLogicException;
import br.com.catdogclinicavet.backend_api.mapper.AgendamentoMapper;
import br.com.catdogclinicavet.backend_api.models.Agendamento;
import br.com.catdogclinicavet.backend_api.models.Animal;
import br.com.catdogclinicavet.backend_api.models.Usuario;
import br.com.catdogclinicavet.backend_api.repositories.AgendamentoRepository;
import br.com.catdogclinicavet.backend_api.repositories.AnimalRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import br.com.catdogclinicavet.backend_api.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private AnimalRepository animalRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AgendamentoMapper agendamentoMapper;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;
    @Mock private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve impedir agendamento se o animal não pertencer ao cliente")
    void create_WhenAnimalNotBelongsToUser_ShouldThrowException() {
        // Arrange
        Long loggedUserId = 10L;
        Long otherUserId = 99L;
        Long animalId = 500L;

        // Mock do usuário logado
        lenient().when(userDetails.getId()).thenReturn(loggedUserId);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock do Cliente no banco
        Usuario clienteLogado = new Usuario();
        clienteLogado.setId(loggedUserId);
        when(usuarioRepository.findById(loggedUserId)).thenReturn(Optional.of(clienteLogado));

        // Mock do Animal (que pertence a OUTRO usuário)
        Animal animalDeOutro = new Animal();
        animalDeOutro.setId(animalId);
        Usuario outroDono = new Usuario();
        outroDono.setId(otherUserId); // ID diferente do logado
        animalDeOutro.setUsuario(outroDono);

        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animalDeOutro));

        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(LocalDateTime.now().plusDays(1), "Checkup", animalId, null);

        // Act & Assert
        assertThrows(BusinessLogicException.class, () -> agendamentoService.create(dto));
        verify(agendamentoRepository, never()).save(any());
    }
}