package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.request.TipoItemRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.TipoItemResponseDTO;
import br.com.catdogclinicavet.backend_api.security.AppRoles;
import br.com.catdogclinicavet.backend_api.service.TipoItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-itens")
public class TipoItemController {

    @Autowired
    private TipoItemService tipoItemService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TipoItemResponseDTO>> findAll() {
        return ResponseEntity.ok(tipoItemService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TipoItemResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoItemService.findById(id));
    }

    @PostMapping("/interno")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<TipoItemResponseDTO> create(@Valid @RequestBody TipoItemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoItemService.create(dto));
    }

    @PutMapping("/interno/{id}")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<TipoItemResponseDTO> update(@PathVariable Long id, @Valid @RequestBody TipoItemRequestDTO dto) {
        return ResponseEntity.ok(tipoItemService.update(id, dto));
    }

    @DeleteMapping("/interno/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public void delete(@PathVariable Long id) {
        tipoItemService.delete(id);
    }
}