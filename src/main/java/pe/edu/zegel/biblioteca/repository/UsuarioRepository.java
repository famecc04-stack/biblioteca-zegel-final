package pe.edu.zegel.biblioteca.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.zegel.biblioteca.entity.Usuario;

/**
 * Repositorio JPA para operaciones sobre la entidad Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles ORDER BY u.id ASC")
    List<Usuario> findAllWithRoles();
}
