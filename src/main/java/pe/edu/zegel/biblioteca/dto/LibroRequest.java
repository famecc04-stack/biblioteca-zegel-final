package pe.edu.zegel.biblioteca.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para crear o actualizar un libro en el catálogo.
 */
public class LibroRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 2, max = 200, message = "El título debe tener entre 2 y 200 caracteres")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(min = 2, max = 150, message = "El autor debe tener entre 2 y 150 caracteres")
    private String autor;

    @NotBlank(message = "El ISBN es obligatorio")
    @Size(min = 5, max = 30, message = "El ISBN debe tener entre 5 y 30 caracteres")
    private String isbn;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(min = 2, max = 80, message = "La categoría debe tener entre 2 y 80 caracteres")
    private String categoria;

    @NotNull(message = "El stock total es obligatorio")
    @Min(value = 1, message = "El stock total debe ser al menos 1")
    private Integer stockTotal;

    public LibroRequest() {
    }

    public LibroRequest(String titulo, String autor, String isbn, String categoria, Integer stockTotal) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.stockTotal = stockTotal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }
}
