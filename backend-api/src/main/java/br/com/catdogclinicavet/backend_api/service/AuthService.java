package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.auth.AuthRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.auth.AuthResponseDTO;
import br.com.catdogclinicavet.backend_api.dto.auth.RegisterRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.UsuarioResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.DuplicateResourceException;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.AuthMapper;
import br.com.catdogclinicavet.backend_api.mapper.UsuarioMapper;
import br.com.catdogclinicavet.backend_api.models.Pessoa;
import br.com.catdogclinicavet.backend_api.models.Role;
import br.com.catdogclinicavet.backend_api.models.Usuario;
import br.com.catdogclinicavet.backend_api.repositories.PessoaRepository;
import br.com.catdogclinicavet.backend_api.repositories.RoleRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import br.com.catdogclinicavet.backend_api.security.UserDetailsImpl;
import br.com.catdogclinicavet.backend_api.security.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.rabbitmq.queue.registration}")
    private String registrationQueue;

    @Transactional
    public UsuarioResponseDTO register(RegisterRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Error: Email is already in use!");
        }

        if (dto.cpfcnpj() != null && !dto.cpfcnpj().isEmpty() && pessoaRepository.existsByCpfcnpj(dto.cpfcnpj())) {
            throw new DuplicateResourceException("Error: CPF/CNPJ is already in use!");
        }

        Role clienteRole = roleRepository.findByNome("CLIENTE")
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role 'CLIENTE' not found."));

        Pessoa novaPessoa = authMapper.toPessoa(dto);
        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        String hashedPassword = passwordEncoder.encode(dto.senha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(hashedPassword);
        novoUsuario.setPessoa(pessoaSalva);
        novoUsuario.setRole(clienteRole);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        try {
            String message = objectMapper.writeValueAsString(usuarioMapper.toResponseDTO(usuarioSalvo));
            rabbitTemplate.convertAndSend(registrationQueue, message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponseDTO registerEmployee(br.com.catdogclinicavet.backend_api.dto.auth.EmployeeRegisterRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Error: Email is already in use!");
        }

        if (dto.cpfcnpj() != null && !dto.cpfcnpj().isEmpty() && pessoaRepository.existsByCpfcnpj(dto.cpfcnpj())) {
            throw new DuplicateResourceException("Error: CPF/CNPJ is already in use!");
        }

        Role role = roleRepository.findByNome(dto.role().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role '" + dto.role() + "' not found."));

        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome(dto.nome());
        novaPessoa.setTelefone(dto.telefone());
        novaPessoa.setCpfcnpj(dto.cpfcnpj());
        novaPessoa.setLogradouro(dto.logradouro());
        novaPessoa.setNumero(dto.numero());
        novaPessoa.setBairro(dto.bairro());
        novaPessoa.setCidade(dto.cidade());
        novaPessoa.setUf(dto.uf());
        novaPessoa.setCep(dto.cep());

        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        String hashedPassword = passwordEncoder.encode(dto.senha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(hashedPassword);
        novoUsuario.setPessoa(pessoaSalva);
        novoUsuario.setRole(role);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    public AuthResponseDTO login(AuthRequestDTO dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        String token = tokenService.generateToken(userDetails);

        return new AuthResponseDTO(token);
    }
}