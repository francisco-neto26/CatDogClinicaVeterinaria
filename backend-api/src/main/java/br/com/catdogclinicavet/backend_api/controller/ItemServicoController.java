package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.request.ItemServicoRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.ItemServicoResponseDTO;
import br.com.catdogclinicavet.backend_api.service.ItemServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/itens-servicos")
public class ItemServicoController {

    @Autowired
    private ItemServicoService itemServicoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemServicoResponseDTO>> findAll() {
        return ResponseEntity.ok(itemServicoService.findAll());
    }

    @GetMapping("/tipo/{tipoItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemServicoResponseDTO>> findByTipo(@PathVariable Long tipoItemId) {
        return ResponseEntity.ok(itemServicoService.findByTipo(tipoItemId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<ItemServicoResponseDTO> create(@Valid @RequestBody ItemServicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemServicoService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<ItemServicoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ItemServicoRequestDTO dto) {
        return ResponseEntity.ok(itemServicoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public void delete(@PathVariable Long id) {
        itemServicoService.delete(id);
    }
}