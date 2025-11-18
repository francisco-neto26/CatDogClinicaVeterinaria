package br.com.catdogclinicavet.backend_api.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "conta_item")
public class ContaItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_servico_id", nullable = false)
    private ItemServico itemServico;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario_momento", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioMomento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public ContaItem() {
    }

    public ContaItem(Conta conta, ItemServico itemServico, Integer quantidade, BigDecimal precoUnitarioMomento, BigDecimal subtotal) {
        this.conta = conta;
        this.itemServico = itemServico;
        this.quantidade = quantidade;
        this.precoUnitarioMomento = precoUnitarioMomento;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public ItemServico getItemServico() {
        return itemServico;
    }

    public void setItemServico(ItemServico itemServico) {
        this.itemServico = itemServico;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitarioMomento() {
        return precoUnitarioMomento;
    }

    public void setPrecoUnitarioMomento(BigDecimal precoUnitarioMomento) {
        this.precoUnitarioMomento = precoUnitarioMomento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaItem contaItem = (ContaItem) o;
        return Objects.equals(id, contaItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}