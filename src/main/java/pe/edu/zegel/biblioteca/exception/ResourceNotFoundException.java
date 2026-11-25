package pe.edu.zegel.biblioteca.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la base de datos (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
