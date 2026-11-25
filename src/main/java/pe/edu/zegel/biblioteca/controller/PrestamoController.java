package pe.edu.zegel.biblioteca.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.zegel.biblioteca.dto.PrestamoRequest;
import pe.edu.zegel.biblioteca.dto.PrestamoResponse;
import pe.edu.zegel.biblioteca.service.PrestamoService;

/**
 * Controlador REST para el registro, consulta y devolución de préstamos de libros.
 */
@RestController
@RequestMapping("/api/prestamos")
@Tag(name = "Prestamos", description = "Gestion y seguimiento de prestamos y devoluciones")
@SecurityRequirement(name = "bearerAuth")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo préstamo", description = "Crea un préstamo y descuenta stock disponible (Solo ADMIN).")
    public ResponseEntity<PrestamoResponse> registrarPrestamo(@Valid @RequestBody PrestamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.registrarPrestamo(request));
    }

    @PutMapping("/{id}/devolver")
    @Operation(summary = "Devolver préstamo", description = "Registra la devolución del libro y repone el stock disponible (Solo ADMIN).")
    public ResponseEntity<PrestamoResponse> devolverPrestamo(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolverPrestamo(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos los préstamos", description = "Obtiene el historial global de préstamos del sistema (Solo ADMIN).")
    public ResponseEntity<List<PrestamoResponse>> listarTodos() {
        return ResponseEntity.ok(prestamoService.listarTodos());
    }

    @GetMapping("/mis-prestamos")
    @Operation(summary = "Consultar mis préstamos", description = "Obtiene el historial de préstamos del usuario autenticado a partir del token JWT (ROLE_USUARIO o ROLE_ADMIN).")
    public ResponseEntity<List<PrestamoResponse>> listarMisPrestamos(Principal principal) {
        String emailUsuario = principal.getName();
        return ResponseEntity.ok(prestamoService.listarMisPrestamos(emailUsuario));
    }
}
