package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.request.UserCreateRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.request.UserUpdateRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.UsuarioResponseDTO;
import br.com.catdogclinicavet.backend_api.security.AppRoles;
import br.com.catdogclinicavet.backend_api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/interno")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/interno/{id}")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping("/interno")
    @PreAuthorize(AppRoles.ACESSO_INTERNO)
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UserCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(dto));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getMe() {
        return ResponseEntity.ok(usuarioService.getMe());
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> updateProfile(@Valid @RequestBody UserUpdateRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.updateProfile(dto));
    }

    @PatchMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> updateFoto(@RequestPart("foto") MultipartFile foto) {
        return ResponseEntity.ok(usuarioService.updateFotoPerfil(foto));
    }
}