package pe.edu.zegel.biblioteca.exception;

/**
 * Excepción lanzada cuando un token JWT es inválido, expirado o de tipo incorrecto (HTTP 401).
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
