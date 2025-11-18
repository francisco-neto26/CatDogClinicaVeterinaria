package br.com.catdogclinicavet.backend_api.models;

import br.com.catdogclinicavet.backend_api.models.enums.ContaStatus;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "conta")
public class Conta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_emissao", nullable = false, updatable = false)
    private LocalDateTime dataEmissao;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ContaStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id", nullable = false, unique = true)
    private Agendamento agendamento;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContaItem> itens = new ArrayList<>();

    // Títulos/Parcelas gerados por esta conta
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TituloReceber> titulos = new ArrayList<>();

    public Conta() {
    }

    public Conta(LocalDateTime dataEmissao, BigDecimal valorTotal, ContaStatus status, Usuario cliente, Agendamento agendamento) {
        this.dataEmissao = dataEmissao;
        this.valorTotal = valorTotal;
        this.status = status;
        this.cliente = cliente;
        this.agendamento = agendamento;
    }

    public void addItem(ContaItem item) {
        itens.add(item);
        item.setConta(this);
    }

    public void removeItem(ContaItem item) {
        itens.remove(item);
        item.setConta(null);
    }

    public void addTitulo(TituloReceber titulo) {
        titulos.add(titulo);
        titulo.setConta(this);
    }

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public ContaStatus getStatus() {
        return status;
    }

    public void setStatus(ContaStatus status) {
        this.status = status;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public List<ContaItem> getItens() {
        return itens;
    }

    public void setItens(List<ContaItem> itens) {
        this.itens = itens;
    }

    public List<TituloReceber> getTitulos() {
        return titulos;
    }

    public void setTitulos(List<TituloReceber> titulos) {
        this.titulos = titulos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(id, conta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}