package br.com.catdogclinicavet.backend_api.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Animal> animais = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<Agendamento> agendamentosComoCliente = new ArrayList<>();

    @OneToMany(mappedBy = "funcionario")
    private List<Agendamento> agendamentosComoFuncionario = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<Conta> contas = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String email, String senha, Pessoa pessoa, Role role) {
        this.email = email;
        this.senha = senha;
        this.pessoa = pessoa;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }

    public List<Agendamento> getAgendamentosComoCliente() {
        return agendamentosComoCliente;
    }

    public void setAgendamentosComoCliente(List<Agendamento> agendamentosComoCliente) {
        this.agendamentosComoCliente = agendamentosComoCliente;
    }

    public List<Agendamento> getAgendamentosComoFuncionario() {
        return agendamentosComoFuncionario;
    }

    public void setAgendamentosComoFuncionario(List<Agendamento> agendamentosComoFuncionario) {
        this.agendamentosComoFuncionario = agendamentosComoFuncionario;
    }

    public List<Conta> getContas() {
        return contas;
    }

    public void setContas(List<Conta> contas) {
        this.contas = contas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}