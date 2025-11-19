package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.request.ContaItemRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.ContaResponseDTO;
import br.com.catdogclinicavet.backend_api.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping("/abrir/{agendamentoId}")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<ContaResponseDTO> abrirConta(@PathVariable Long agendamentoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.abrirConta(agendamentoId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.findById(id));
    }

    @PostMapping("/{id}/itens")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<ContaResponseDTO> adicionarItem(@PathVariable Long id, @Valid @RequestBody ContaItemRequestDTO dto) {
        return ResponseEntity.ok(contaService.adicionarItem(id, dto));
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<ContaResponseDTO> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        return ResponseEntity.ok(contaService.removerItem(id, itemId));
    }

    @PostMapping("/{id}/fechar")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'MEDICO VETERINARIO')")
    public ResponseEntity<ContaResponseDTO> fecharConta(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.fecharConta(id));
    }
}