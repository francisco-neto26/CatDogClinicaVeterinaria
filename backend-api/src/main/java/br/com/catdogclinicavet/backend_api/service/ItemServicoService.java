package br.com.catdogclinicavet.backend_api.service;

import br.com.catdogclinicavet.backend_api.dto.request.ItemServicoRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.ItemServicoResponseDTO;
import br.com.catdogclinicavet.backend_api.exceptions.ResourceNotFoundException;
import br.com.catdogclinicavet.backend_api.mapper.ItemServicoMapper;
import br.com.catdogclinicavet.backend_api.models.ItemServico;
import br.com.catdogclinicavet.backend_api.models.TipoItem;
import br.com.catdogclinicavet.backend_api.repositories.ItemServicoRepository;
import br.com.catdogclinicavet.backend_api.repositories.TipoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServicoService {

    @Autowired
    private ItemServicoRepository itemServicoRepository;

    @Autowired
    private TipoItemRepository tipoItemRepository;

    @Autowired
    private ItemServicoMapper itemServicoMapper;

    @Cacheable(value = "itens-servicos", key = "'all'") 
    public List<ItemServicoResponseDTO> findAll() {
        return itemServicoRepository.findAll().stream()
                .map(itemServicoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "itens-servicos-tipo", key = "#tipoItemId")
    public List<ItemServicoResponseDTO> findByTipo(Long tipoItemId) {
        return itemServicoRepository.findByTipoItemId(tipoItemId).stream()
                .map(itemServicoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"itens-servicos", "itens-servicos-tipo"}, allEntries = true)
    public ItemServicoResponseDTO create(ItemServicoRequestDTO dto) {
        TipoItem tipoItem = tipoItemRepository.findById(dto.tipoItemId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoItem não encontrado."));

        ItemServico item = itemServicoMapper.toEntity(dto);
        item.setTipoItem(tipoItem);

        return itemServicoMapper.toResponseDTO(itemServicoRepository.save(item));
    }

    @Transactional
    @CacheEvict(value = {"itens-servicos", "itens-servicos-tipo"}, allEntries = true)
    public ItemServicoResponseDTO update(Long id, ItemServicoRequestDTO dto) {
        ItemServico item = itemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemServico não encontrado."));

        TipoItem tipoItem = tipoItemRepository.findById(dto.tipoItemId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoItem não encontrado."));

        itemServicoMapper.updateEntityFromDto(dto, item);
        item.setTipoItem(tipoItem);

        return itemServicoMapper.toResponseDTO(itemServicoRepository.save(item));
    }

    @CacheEvict(value = {"itens-servicos", "itens-servicos-tipo"}, allEntries = true)
    public void delete(Long id) {
        if (!itemServicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("ItemServico não encontrado.");
        }
        itemServicoRepository.deleteById(id);
    }
}