package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.TipoItemRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.TipoItemResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.TipoItemMapper;
import br.com.catdogclinicavet.backend_api.models.TipoItem;
import br.com.catdogclinicavet.backend_api.repositories.TipoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoItemService {

    @Autowired
    private TipoItemRepository tipoItemRepository;

    @Autowired
    private TipoItemMapper tipoItemMapper;

    @Cacheable(value = "tipos-itens", key = "'all'")
    public List<TipoItemResponseDTO> findAll() {
        return tipoItemRepository.findAll().stream()
                .map(tipoItemMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "tipos-itens", allEntries = true)
    public TipoItemResponseDTO create(TipoItemRequestDTO dto) {
        TipoItem tipoItem = tipoItemMapper.toEntity(dto);
        return tipoItemMapper.toResponseDTO(tipoItemRepository.save(tipoItem));
    }

    @Transactional
    @CacheEvict(value = "tipos-itens", allEntries = true)
    public TipoItemResponseDTO update(Long id, TipoItemRequestDTO dto) {
        TipoItem tipoItem = tipoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoItem não encontrado."));

        tipoItemMapper.updateEntityFromDto(dto, tipoItem);
        return tipoItemMapper.toResponseDTO(tipoItemRepository.save(tipoItem));
    }

    @CacheEvict(value = "tipos-itens", allEntries = true)
    public void delete(Long id) {
        if (!tipoItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("TipoItem não encontrado.");
        }
        tipoItemRepository.deleteById(id);
    }

    public TipoItemResponseDTO findById(Long id) {
        TipoItem tipoItem = tipoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoItem não encontrado."));
        return tipoItemMapper.toResponseDTO(tipoItem);
    }
}