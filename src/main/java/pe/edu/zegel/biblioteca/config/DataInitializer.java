package pe.edu.zegel.biblioteca.config;

import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.zegel.biblioteca.entity.Libro;
import pe.edu.zegel.biblioteca.entity.Rol;
import pe.edu.zegel.biblioteca.entity.Usuario;
import pe.edu.zegel.biblioteca.repository.LibroRepository;
import pe.edu.zegel.biblioteca.repository.RolRepository;
import pe.edu.zegel.biblioteca.repository.UsuarioRepository;

/**
 * Inicializador de datos que se ejecuta al iniciar la aplicación y precarga
 * roles, usuarios iniciales y 5 libros de ejemplo de forma idempotente.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            LibroRepository libroRepository,
            PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Rol rolAdmin = crearRolSiNoExiste("ROLE_ADMIN");
        Rol rolUsuario = crearRolSiNoExiste("ROLE_USUARIO");

        crearUsuarioSiNoExiste(
                "Administrador Zegel",
                "admin@zegel.edu.pe",
                "admin123",
                Set.of(rolAdmin)
        );

        crearUsuarioSiNoExiste(
                "Estudiante Zegel",
                "usuario@zegel.edu.pe",
                "user123",
                Set.of(rolUsuario)
        );

        cargarLibrosIniciales();
    }

    private Rol crearRolSiNoExiste(String nombreRol) {
        return rolRepository.findByNombre(nombreRol)
                .orElseGet(() -> rolRepository.save(new Rol(nombreRol)));
    }

    private void crearUsuarioSiNoExiste(String nombre, String email, String rawPassword, Set<Rol> roles) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario usuario = new Usuario(
                    nombre,
                    email,
                    passwordEncoder.encode(rawPassword),
                    true
            );
            usuario.setRoles(roles);
            usuarioRepository.save(usuario);
        }
    }

    private void cargarLibrosIniciales() {
        List<Libro> librosBase = List.of(
                new Libro("Clean Code", "Robert C. Martin", "978-0132350884", "Ingeniería de Software", 5, 5),
                new Libro("Spring Boot in Action", "Craig Walls", "978-1617292545", "Desarrollo Backend", 4, 4),
                new Libro("Design Patterns", "Erich Gamma et al.", "978-0201633610", "Arquitectura de Software", 3, 3),
                new Libro("Effective Java", "Joshua Bloch", "978-0134685991", "Programación Java", 6, 6),
                new Libro("Domain-Driven Design", "Eric Evans", "978-0321125217", "Arquitectura de Software", 2, 2)
        );

        for (Libro libro : librosBase) {
            if (!libroRepository.existsByIsbn(libro.getIsbn())) {
                libroRepository.save(libro);
            }
        }
    }
}
