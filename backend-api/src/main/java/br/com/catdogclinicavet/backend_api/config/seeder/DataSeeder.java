package br.com.catdogclinicavet.backend_api.config.seeder;

import br.com.catdogclinicavet.backend_api.models.Pessoa;
import br.com.catdogclinicavet.backend_api.models.Role;
import br.com.catdogclinicavet.backend_api.models.Usuario;
import br.com.catdogclinicavet.backend_api.repositories.PessoaRepository;
import br.com.catdogclinicavet.backend_api.repositories.RoleRepository;
import br.com.catdogclinicavet.backend_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.existsByEmail("admin@vet.com")) {
            return;
        }

        Role roleFuncionario = roleRepository.findByNome("FUNCIONARIO")
                .orElseThrow(() -> new RuntimeException("Role FUNCIONARIO não encontrada"));

        Pessoa pessoaAdmin = new Pessoa();
        pessoaAdmin.setNome("Administrador do Sistema");
        pessoaAdmin.setCpfcnpj("00000000000");
        pessoaAdmin = pessoaRepository.save(pessoaAdmin);

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setEmail("admin@vet.com");
        usuarioAdmin.setSenha(passwordEncoder.encode("admin"));
        usuarioAdmin.setRole(roleFuncionario);
        usuarioAdmin.setPessoa(pessoaAdmin);

        usuarioRepository.save(usuarioAdmin);

        System.out.println(">>> USUÁRIO ADMIN CRIADO COM SUCESSO: admin@vet.com / admin <<<");
    }
}
