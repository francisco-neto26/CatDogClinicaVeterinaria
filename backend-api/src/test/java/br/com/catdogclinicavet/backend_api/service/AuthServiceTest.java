package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.auth.RegisterRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.UsuarioResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.DuplicateResourceException;
import br.com.catdogclinicavet.backend_api.mapper.AuthMapper;
import br.com.catdogclinicavet.backend_api.mapper.UsuarioMapper;
import br.com.catdogclinicavet.backend_api.models.Pessoa;
import br.com.catdogclinicavet.backend_api.models.Role;
import br.com.catdogclinicavet.backend_api.models.Usuario;
import br.com.catdogclinicavet.backend_api.repositories.PessoaRepository;
import br.com.catdogclinicavet.backend_api.repositories.RoleRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import br.com.catdogclinicavet.backend_api.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils; // Importante

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PessoaRepository pessoaRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthMapper authMapper;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private ObjectMapper objectMapper;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenService tokenService;

-
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "registrationQueue", "vet.registration.queue");
    }


    @Test
    @DisplayName("Deve lançar exceção se tentar registrar com email já existente")
    void register_WhenEmailExists_ShouldThrowException() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Teste", "123", "000", "Rua", "1", "Bairro", "Cidade", "UF", "CEP",
                "duplicado@vet.com", "123456"
        );

        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso e enviar mensagem para fila")
    void register_WhenValid_ShouldSaveAndSendQueue() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Novo", "123", "11122233344", "Rua", "1", "Bairro", "Cidade", "UF", "CEP",
                "novo@vet.com", "123456"
        );

        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(pessoaRepository.existsByCpfcnpj(any())).thenReturn(false);
        when(roleRepository.findByNome("CLIENTE")).thenReturn(Optional.of(new Role("CLIENTE")));
        when(authMapper.toPessoa(dto)).thenReturn(new Pessoa());
        when(pessoaRepository.save(any())).thenReturn(new Pessoa());
        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");

        Usuario savedUser = new Usuario();
        savedUser.setId(1L);
        when(usuarioRepository.save(any())).thenReturn(savedUser);
        when(usuarioMapper.toResponseDTO(any())).thenReturn(new UsuarioResponseDTO(1L, "novo@vet.com", "foto", null, null));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1, \"email\":\"novo@vet.com\"}");

        authService.register(dto);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("vet.registration.queue"), // O valor que injetamos no setUp
                contains("novo@vet.com")      // Parte do JSON que o Mock retornou
        );
    }
}