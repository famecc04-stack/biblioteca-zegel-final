package pe.edu.zegel.biblioteca.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.zegel.biblioteca.entity.Rol;

/**
 * Repositorio JPA para la gestión de roles del sistema.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);
}
