package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.ContaItemRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.ContaResponseDTO;
import br.com.catdogclinicavet.backend_api.mapper.ContaMapper;
import br.com.catdogclinicavet.backend_api.models.Conta;
import br.com.catdogclinicavet.backend_api.models.ItemServico;
import br.com.catdogclinicavet.backend_api.models.enums.ContaStatus;
import br.com.catdogclinicavet.backend_api.repositories.ContaRepository;
import br.com.catdogclinicavet.backend_api.repositories.ItemServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @InjectMocks
    private ContaService contaService;

    @Mock private ContaRepository contaRepository;
    @Mock private ItemServicoRepository itemServicoRepository;
    @Mock private ContaMapper contaMapper;

    @Test
    @DisplayName("Deve atualizar o valor total da conta ao adicionar um item")
    void adicionarItem_ShouldUpdateTotalValue() {

        Long contaId = 1L;
        Long itemId = 10L;
        int quantidade = 2;
        BigDecimal precoUnitario = new BigDecimal("100.00");

        BigDecimal expectedTotal = new BigDecimal("200.00");

        Conta contaAberta = new Conta();
        contaAberta.setId(contaId);
        contaAberta.setStatus(ContaStatus.ABERTA);
        contaAberta.setValorTotal(BigDecimal.ZERO);

        ItemServico itemServico = new ItemServico();
        itemServico.setId(itemId);
        itemServico.setPrecoUnitario(precoUnitario);

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(contaAberta));
        when(itemServicoRepository.findById(itemId)).thenReturn(Optional.of(itemServico));

        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(contaMapper.toResponseDTO(any())).thenReturn(new ContaResponseDTO(1L, null, expectedTotal, "ABERTA", 1L, 1L, null, null));

        ContaItemRequestDTO dto = new ContaItemRequestDTO(itemId, quantidade);

        contaService.adicionarItem(contaId, dto);

        assertEquals(expectedTotal, contaAberta.getValorTotal());
        verify(contaRepository).save(contaAberta);
    }
}