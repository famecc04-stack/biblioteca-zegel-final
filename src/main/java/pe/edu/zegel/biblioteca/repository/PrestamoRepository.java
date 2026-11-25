package pe.edu.zegel.biblioteca.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.zegel.biblioteca.entity.EstadoPrestamo;
import pe.edu.zegel.biblioteca.entity.Prestamo;

/**
 * Repositorio JPA para la gestión y consulta de préstamos bibliotecarios.
 */
@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("""
           SELECT p FROM Prestamo p
           JOIN FETCH p.usuario u
           JOIN FETCH p.libro l
           WHERE u.id = :usuarioId
             AND p.estado = pe.edu.zegel.biblioteca.entity.EstadoPrestamo.ACTIVO
           ORDER BY p.fechaPrestamo DESC
           """)
    List<Prestamo> findPrestamosActivosPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
           SELECT p FROM Prestamo p
           JOIN FETCH p.usuario u
           JOIN FETCH p.libro l
           WHERE u.email = :email
           ORDER BY p.id DESC
           """)
    List<Prestamo> findHistorialPorEmailUsuario(@Param("email") String email);

    @Query("""
           SELECT p FROM Prestamo p
           JOIN FETCH p.usuario u
           JOIN FETCH p.libro l
           ORDER BY p.id DESC
           """)
    List<Prestamo> findAllWithUsuarioAndLibro();

    boolean existsByLibroIdAndEstado(Long libroId, EstadoPrestamo estado);
}
