package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.request.ContaItemRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.ContaResponseDTO;
import br.com.catdogclinicavet.backend_api.security.AppRoles;
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

    @PostMapping("/interno/abrir/{agendamentoId}")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<ContaResponseDTO> abrirConta(@PathVariable Long agendamentoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.abrirConta(agendamentoId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.findById(id));
    }

    @PostMapping("/interno/{id}/itens")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<ContaResponseDTO> adicionarItem(@PathVariable Long id, @Valid @RequestBody ContaItemRequestDTO dto) {
        return ResponseEntity.ok(contaService.adicionarItem(id, dto));
    }

    @DeleteMapping("/interno/{id}/itens/{itemId}")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<ContaResponseDTO> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        return ResponseEntity.ok(contaService.removerItem(id, itemId));
    }

    @PostMapping("/interno/{id}/fechar")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<ContaResponseDTO> fecharConta(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.fecharConta(id));
    }
}