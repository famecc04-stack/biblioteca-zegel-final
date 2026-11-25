package pe.edu.zegel.biblioteca.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.zegel.biblioteca.entity.Libro;

/**
 * Repositorio JPA para la entidad Libro con consultas personalizadas.
 */
@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Libro> findByIsbn(String isbn);

    @Query("SELECT l FROM Libro l WHERE l.stockDisponible > 0 ORDER BY l.titulo ASC")
    List<Libro> findLibrosDisponibles();

    @Query("""
           SELECT l FROM Libro l
           WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
              OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :termino, '%'))
           ORDER BY l.titulo ASC
           """)
    List<Libro> buscarPorTituloOAutor(@Param("termino") String termino);
}
