package pe.edu.zegel.biblioteca.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para registrar el préstamo de un libro a un usuario.
 */
public class PrestamoRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Min(value = 1, message = "El ID del usuario debe ser mayor a 0")
    private Long usuarioId;

    @NotNull(message = "El ID del libro es obligatorio")
    @Min(value = 1, message = "El ID del libro debe ser mayor a 0")
    private Long libroId;

    @NotNull(message = "Los días de préstamo son obligatorios")
    @Min(value = 1, message = "El préstamo debe ser de al menos 1 día")
    private Integer diasPrestamo;

    public PrestamoRequest() {
    }

    public PrestamoRequest(Long usuarioId, Long libroId, Integer diasPrestamo) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.diasPrestamo = diasPrestamo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public Integer getDiasPrestamo() {
        return diasPrestamo;
    }

    public void setDiasPrestamo(Integer diasPrestamo) {
        this.diasPrestamo = diasPrestamo;
    }
}
