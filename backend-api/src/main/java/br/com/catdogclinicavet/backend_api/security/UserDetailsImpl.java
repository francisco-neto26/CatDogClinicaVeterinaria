package br.com.catdogclinicavet.backend_api.security;

import br.com.catdogclinicavet.backend_api.models.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome; // <--- CAMPO NOVO
    private String email;

    @JsonIgnore
    private String password;

    private String fotoUrl;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String nome, String email, String password, String fotoUrl,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.fotoUrl = fotoUrl;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Usuario usuario) {
        String roleName = usuario.getRole().getNome();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getPessoa().getNome(), // <--- PEGA O NOME DA PESSOA
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getFotoUrl(),
                Collections.singletonList(authority)
        );
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    } // <--- GETTER NOVO

    public String getFotoUrl() {
        return fotoUrl;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}