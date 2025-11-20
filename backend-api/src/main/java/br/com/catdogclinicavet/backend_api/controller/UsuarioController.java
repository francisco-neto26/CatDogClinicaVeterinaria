package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.response.UsuarioResponseDTO;
import br.com.catdogclinicavet.backend_api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getMe() {
        return ResponseEntity.ok(usuarioService.getMe());
    }

    @PatchMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> updateFoto(@RequestPart("foto") MultipartFile foto) {
        return ResponseEntity.ok(usuarioService.updateFotoPerfil(foto));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> updateProfile(@jakarta.validation.Valid @RequestBody br.com.catdogclinicavet.backend_api.dto.request.UserUpdateRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.updateProfile(dto));
    }

}