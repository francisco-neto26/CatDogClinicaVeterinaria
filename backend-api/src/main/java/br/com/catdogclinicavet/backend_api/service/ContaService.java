package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.ContaItemRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.ContaResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.BusinessLogicException;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.ContaMapper;
import br.com.catdogclinicavet.backend_api.models.*;
import br.com.catdogclinicavet.backend_api.models.enums.ContaStatus;
import br.com.catdogclinicavet.backend_api.models.enums.TituloStatus;
import br.com.catdogclinicavet.backend_api.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ItemServicoRepository itemServicoRepository;

    @Autowired
    private TituloReceberRepository tituloReceberRepository;

    @Autowired
    private ContaMapper contaMapper;

    public List<ContaResponseDTO> findAll(Long clienteId) {
        List<Conta> contas;
        if (clienteId != null) {
            contas = contaRepository.findByClienteId(clienteId);
        } else {
            contas = contaRepository.findAll();
        }

        return contas.stream()
                .map(contaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContaResponseDTO abrirConta(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));

        Optional<Conta> contaExistente = contaRepository.findByAgendamentoId(agendamentoId);
        if (contaExistente.isPresent()) {
            return contaMapper.toResponseDTO(contaExistente.get());
        }

        Conta conta = new Conta();
        conta.setAgendamento(agendamento);
        conta.setCliente(agendamento.getCliente());
        conta.setDataEmissao(LocalDateTime.now());
        conta.setStatus(ContaStatus.ABERTA);
        conta.setValorTotal(BigDecimal.ZERO);

        return contaMapper.toResponseDTO(contaRepository.save(conta));
    }

    @Transactional
    public ContaResponseDTO adicionarItem(Long contaId, ContaItemRequestDTO dto) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));

        if (conta.getStatus() != ContaStatus.ABERTA) {
            throw new BusinessLogicException("Não é possível adicionar itens a uma conta fechada ou cancelada.");
        }

        ItemServico itemServico = itemServicoRepository.findById(dto.itemServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Item de serviço não encontrado."));

        BigDecimal precoMomento = itemServico.getPrecoUnitario();
        BigDecimal subtotal = precoMomento.multiply(new BigDecimal(dto.quantidade()));

        ContaItem contaItem = new ContaItem();
        contaItem.setConta(conta);
        contaItem.setItemServico(itemServico);
        contaItem.setQuantidade(dto.quantidade());
        contaItem.setPrecoUnitarioMomento(precoMomento);
        contaItem.setSubtotal(subtotal);

        conta.addItem(contaItem);
        conta.setValorTotal(conta.getValorTotal().add(subtotal));

        return contaMapper.toResponseDTO(contaRepository.save(conta));
    }

    @Transactional
    public ContaResponseDTO removerItem(Long contaId, Long itemId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));

        if (conta.getStatus() != ContaStatus.ABERTA) {
            throw new BusinessLogicException("Não é possível remover itens de uma conta fechada.");
        }

        ContaItem itemParaRemover = conta.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado na conta."));

        conta.setValorTotal(conta.getValorTotal().subtract(itemParaRemover.getSubtotal()));
        conta.removeItem(itemParaRemover);

        return contaMapper.toResponseDTO(contaRepository.save(conta));
    }

    @Transactional
    public ContaResponseDTO fecharConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));

        if (conta.getStatus() != ContaStatus.ABERTA) {
            throw new BusinessLogicException("Conta já está fechada ou cancelada.");
        }

        if (conta.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("Não é possível fechar uma conta com valor zero.");
        }

        conta.setStatus(ContaStatus.FECHADA);

        TituloReceber titulo = new TituloReceber();
        titulo.setConta(conta);
        titulo.setCliente(conta.getCliente());
        titulo.setValor(conta.getValorTotal());
        titulo.setDataEmissao(LocalDate.now());
        titulo.setDataVencimento(LocalDate.now().plusDays(7));
        titulo.setStatus(TituloStatus.PENDENTE);

        tituloReceberRepository.save(titulo);

        return contaMapper.toResponseDTO(contaRepository.save(conta));
    }

    public ContaResponseDTO findById(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));
        return contaMapper.toResponseDTO(conta);
    }
}