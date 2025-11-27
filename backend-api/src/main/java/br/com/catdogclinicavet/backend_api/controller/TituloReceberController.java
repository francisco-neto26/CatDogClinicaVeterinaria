package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.response.TituloReceberResponseDTO;
import br.com.catdogclinicavet.backend_api.security.AppRoles;
import br.com.catdogclinicavet.backend_api.service.TituloReceberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/titulos")
public class TituloReceberController {

    @Autowired
    private TituloReceberService tituloReceberService;

    @GetMapping("/interno")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<List<TituloReceberResponseDTO>> findAll() {
        return ResponseEntity.ok(tituloReceberService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TituloReceberResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tituloReceberService.findById(id));
    }

    @PatchMapping("/interno/{id}/baixa")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<TituloReceberResponseDTO> darBaixa(@PathVariable Long id) {
        return ResponseEntity.ok(tituloReceberService.darBaixa(id));
    }

    @PatchMapping("/interno/{id}/cancelar")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<TituloReceberResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(tituloReceberService.cancelar(id));
    }
}