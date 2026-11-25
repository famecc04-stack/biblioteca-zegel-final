package pe.edu.zegel.biblioteca.exception;

import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones que transforma errores de la aplicación en respuestas JSON uniformes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Error de validación en los datos enviados",
                detalles
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud JSON tiene un formato inválido",
                List.of("Verifique la sintaxis JSON y los tipos de datos enviados")
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                List.of("El recurso solicitado no fue encontrado")
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                List.of("Conflicto con una regla de negocio del sistema")
        );
    }

    @ExceptionHandler({
            InvalidTokenException.class,
            BadCredentialsException.class,
            AuthenticationException.class,
            JwtException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationErrors(Exception ex) {
        String mensaje = ex instanceof BadCredentialsException
                ? "Credenciales incorrectas: email o contraseña inválidos"
                : ex.getMessage();

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                mensaje,
                List.of("Verifique sus credenciales o su token JWT")
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "Acceso denegado: no tiene permisos suficientes para realizar esta operación",
                List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno inesperado en el servidor",
                List.of(ex.getMessage() != null ? ex.getMessage() : "Error desconocido")
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String mensaje, List<String> detalles) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                detalles
        );
        return ResponseEntity.status(status).body(body);
    }
}
