package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.UserCreateRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.request.UserUpdateRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.UsuarioResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.DuplicateResourceException;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.UsuarioMapper;
import br.com.catdogclinicavet.backend_api.models.Pessoa;
import br.com.catdogclinicavet.backend_api.models.Role;
import br.com.catdogclinicavet.backend_api.models.Usuario;
import br.com.catdogclinicavet.backend_api.repositories.PessoaRepository;
import br.com.catdogclinicavet.backend_api.repositories.RoleRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import br.com.catdogclinicavet.backend_api.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UsuarioMapper usuarioMapper;

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO create(br.com.catdogclinicavet.backend_api.dto.request.UserCreateRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Email já está em uso.");
        }
        if (dto.cpfcnpj() != null && !dto.cpfcnpj().isEmpty() && pessoaRepository.existsByCpfcnpj(dto.cpfcnpj())) {
            throw new DuplicateResourceException("CPF/CNPJ já está em uso.");
        }

        Role role = roleRepository.findByNome(dto.role().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + dto.role()));

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setTelefone(dto.telefone());
        pessoa.setCpfcnpj(dto.cpfcnpj());
        pessoa.setLogradouro(dto.logradouro());
        pessoa.setNumero(dto.numero());
        pessoa.setBairro(dto.bairro());
        pessoa.setCidade(dto.cidade());
        pessoa.setUf(dto.uf());
        pessoa.setCep(dto.cep());

        pessoa = pessoaRepository.save(pessoa);

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(role);
        usuario.setPessoa(pessoa);

        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO updateProfile(UserUpdateRequestDTO dto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        var pessoa = usuario.getPessoa();
        pessoa.setNome(dto.nome());
        pessoa.setTelefone(dto.telefone());
        pessoa.setLogradouro(dto.logradouro());
        pessoa.setNumero(dto.numero());
        pessoa.setBairro(dto.bairro());
        pessoa.setCidade(dto.cidade());
        pessoa.setUf(dto.uf());
        pessoa.setCep(dto.cep());

        usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO updateFotoPerfil(MultipartFile file) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isBlank()) {
            try {
                String oldUrl = usuario.getFotoUrl();
                String oldFileName = oldUrl.substring(oldUrl.lastIndexOf("/") + 1);
                storageService.deleteFile(oldFileName);
            } catch (Exception e) {
                System.err.println("Falha ao limpar foto antiga: " + e.getMessage());
            }
        }

        String fotoUrl = storageService.uploadFile(file);
        usuario.setFotoUrl(fotoUrl);

        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO getMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }
}