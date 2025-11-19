package br.com.catdogclinicavet.backend_api.controller;

import br.com.catdogclinicavet.backend_api.dto.auth.AuthRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.auth.AuthResponseDTO;
import br.com.catdogclinicavet.backend_api.dto.auth.RegisterRequestDTO;
import br.com.catdogclinicavet.backend_api.dto.response.UsuarioResponseDTO;
import br.com.catdogclinicavet.backend_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        UsuarioResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register-employee")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<UsuarioResponseDTO> registerEmployee(@Valid @RequestBody br.com.catdogclinicavet.backend_api.dto.auth.EmployeeRegisterRequestDTO dto) {
        UsuarioResponseDTO response = authService.registerEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}