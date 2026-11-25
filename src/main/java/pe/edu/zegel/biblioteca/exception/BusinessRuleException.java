package pe.edu.zegel.biblioteca.exception;

/**
 * Excepción lanzada ante violaciones de reglas de negocio como falta de stock o registros duplicados (HTTP 409).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
