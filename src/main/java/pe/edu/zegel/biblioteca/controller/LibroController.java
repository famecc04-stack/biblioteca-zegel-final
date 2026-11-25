package pe.edu.zegel.biblioteca.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.zegel.biblioteca.dto.LibroRequest;
import pe.edu.zegel.biblioteca.dto.LibroResponse;
import pe.edu.zegel.biblioteca.service.LibroService;

/**
 * Controlador REST para la gestión del catálogo de libros.
 * Las consultas son de acceso público, mientras que las operaciones
 * de escritura/modificación/eliminación requieren el rol ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/libros")
@Tag(name = "Libros", description = "Catálogo y administración de libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    @Operation(summary = "Listar libros", description = "Permite listar todos los libros, solo los disponibles o buscar por título/autor.")
    public ResponseEntity<List<LibroResponse>> listarLibros(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false, defaultValue = "false") boolean soloDisponibles) {

        if (buscar != null && !buscar.isBlank()) {
            return ResponseEntity.ok(libroService.buscar(buscar));
        }
        if (soloDisponibles) {
            return ResponseEntity.ok(libroService.listarDisponibles());
        }
        return ResponseEntity.ok(libroService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por ID", description = "Consulta los detalles de un libro específico.")
    public ResponseEntity<LibroResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Registrar nuevo libro", description = "Permite registrar un nuevo libro en el catálogo (Solo ADMIN).")
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crear(request));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar libro", description = "Modifica los datos y actualiza el stock de un libro (Solo ADMIN).")
    public ResponseEntity<LibroResponse> actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(libroService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar libro", description = "Elimina un libro si no tiene préstamos activos ni historial registrado (Solo ADMIN).")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
