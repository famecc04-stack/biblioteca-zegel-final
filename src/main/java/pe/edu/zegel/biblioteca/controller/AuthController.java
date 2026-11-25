package pe.edu.zegel.biblioteca.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.zegel.biblioteca.dto.AuthResponse;
import pe.edu.zegel.biblioteca.dto.LoginRequest;
import pe.edu.zegel.biblioteca.dto.RefreshTokenRequest;
import pe.edu.zegel.biblioteca.dto.RegisterRequest;
import pe.edu.zegel.biblioteca.service.AuthService;

/**
 * Endpoints públicos de autenticación y emisión de tokens JWT.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Registro, login y renovacion de JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(summary = "Registrar usuario", description = "Registra un estudiante con rol ROLE_USUARIO.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Iniciar sesion", description = "Valida credenciales y genera Access Token y Refresh Token.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @SecurityRequirements
    @Operation(summary = "Renovar token", description = "Genera un nuevo Access Token usando el Refresh Token.")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
