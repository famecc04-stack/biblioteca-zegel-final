package pe.edu.zegel.biblioteca.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI / Swagger UI con soporte para autenticación Bearer JWT.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Sistema de Gestión de Biblioteca - Zegel",
                version = "1.0.0",
                description = "Backend RESTful para la gestión de catálogo bibliográfico, préstamos, devoluciones "
                        + "y control de acceso basado en roles (RBAC) con JWT y H2 Database.",
                contact = @Contact(
                        name = "Instituto Zegel - Desarrollo de los Componentes del Negocio",
                        email = "admin@zegel.edu.pe"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Ingrese el Access Token JWT obtenido en /api/auth/login (sin el prefijo 'Bearer ')."
)
public class OpenApiConfig {
}
