package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.request.AgendamentoRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.AgendamentoResponseDTO;
import br.com.catdogclinicavet.backend_api.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AgendamentoResponseDTO> create(@Valid @RequestBody AgendamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.create(dto));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<AgendamentoResponseDTO>> listMine(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(agendamentoService.listMine(pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<Page<AgendamentoResponseDTO>> listAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(agendamentoService.listAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AgendamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.findById(id));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        agendamentoService.cancel(id);
    }

    @PatchMapping("/{id}/atribuir-veterinario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<AgendamentoResponseDTO> assignVeterinarian(@PathVariable Long id, @PathVariable Long funcionarioId) {
        return ResponseEntity.ok(agendamentoService.assignVeterinarian(id, funcionarioId));
    }

    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<AgendamentoResponseDTO> complete(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.complete(id));
    }
}