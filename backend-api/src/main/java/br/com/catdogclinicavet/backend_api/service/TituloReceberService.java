package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.response.TituloReceberResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.BusinessLogicException;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.TituloReceberMapper;
import br.com.catdogclinicavet.backend_api.models.TituloReceber;
import br.com.catdogclinicavet.backend_api.models.enums.TituloStatus;
import br.com.catdogclinicavet.backend_api.repositories.TituloReceberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TituloReceberService {

    @Autowired
    private TituloReceberRepository tituloReceberRepository;

    @Autowired
    private TituloReceberMapper tituloReceberMapper;

    public List<TituloReceberResponseDTO> findAll() {
        return tituloReceberRepository.findAll().stream()
                .map(tituloReceberMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public TituloReceberResponseDTO findById(Long id) {
        TituloReceber titulo = tituloReceberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Título não encontrado."));
        return tituloReceberMapper.toResponseDTO(titulo);
    }

    @Transactional
    public TituloReceberResponseDTO darBaixa(Long id) {
        TituloReceber titulo = tituloReceberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Título não encontrado."));

        if (titulo.getStatus() == TituloStatus.PAGO || titulo.getStatus() == TituloStatus.CANCELADO) {
            throw new BusinessLogicException("Título já está pago ou cancelado.");
        }

        titulo.setStatus(TituloStatus.PAGO);
        titulo.setDataPagamento(LocalDate.now());

        return tituloReceberMapper.toResponseDTO(tituloReceberRepository.save(titulo));
    }

    @Transactional
    public TituloReceberResponseDTO cancelar(Long id) {
        TituloReceber titulo = tituloReceberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Título não encontrado."));

        if (titulo.getStatus() == TituloStatus.PAGO) {
            throw new BusinessLogicException("Não é possível cancelar um título já pago.");
        }

        titulo.setStatus(TituloStatus.CANCELADO);
        return tituloReceberMapper.toResponseDTO(tituloReceberRepository.save(titulo));
    }
}