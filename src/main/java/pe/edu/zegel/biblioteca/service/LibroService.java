package pe.edu.zegel.biblioteca.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.zegel.biblioteca.dto.LibroRequest;
import pe.edu.zegel.biblioteca.dto.LibroResponse;
import pe.edu.zegel.biblioteca.entity.EstadoPrestamo;
import pe.edu.zegel.biblioteca.entity.Libro;
import pe.edu.zegel.biblioteca.exception.BusinessRuleException;
import pe.edu.zegel.biblioteca.exception.ResourceNotFoundException;
import pe.edu.zegel.biblioteca.repository.LibroRepository;
import pe.edu.zegel.biblioteca.repository.PrestamoRepository;

/**
 * Servicio de negocio para la administración y consulta del catálogo de libros.
 */
@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public LibroService(LibroRepository libroRepository, PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> listarTodos() {
        return libroRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> listarDisponibles() {
        return libroRepository.findLibrosDisponibles().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> buscar(String termino) {
        return libroRepository.buscarPorTituloOAutor(termino == null ? "" : termino.trim()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LibroResponse obtenerPorId(Long id) {
        Libro libro = buscarEntidadPorId(id);
        return mapToResponse(libro);
    }

    @Transactional
    public LibroResponse crear(LibroRequest request) {
        String isbnLimpio = request.getIsbn().trim();
        if (libroRepository.existsByIsbn(isbnLimpio)) {
            throw new BusinessRuleException("Ya existe un libro registrado con el ISBN: " + isbnLimpio);
        }

        Libro libro = new Libro(
                request.getTitulo().trim(),
                request.getAutor().trim(),
                isbnLimpio,
                request.getCategoria().trim(),
                request.getStockTotal(),
                request.getStockTotal()
        );

        return mapToResponse(libroRepository.save(libro));
    }

    @Transactional
    public LibroResponse actualizar(Long id, LibroRequest request) {
        Libro libro = buscarEntidadPorId(id);
        String isbnLimpio = request.getIsbn().trim();

        libroRepository.findByIsbn(isbnLimpio).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new BusinessRuleException("El ISBN '" + isbnLimpio + "' ya pertenece a otro libro");
            }
        });

        int ejemplaresPrestados = libro.getStockTotal() - libro.getStockDisponible();
        if (request.getStockTotal() < ejemplaresPrestados) {
            throw new BusinessRuleException(
                    "El stock total no puede ser menor a la cantidad de ejemplares actualmente prestados ("
                            + ejemplaresPrestados + ")"
            );
        }

        libro.setTitulo(request.getTitulo().trim());
        libro.setAutor(request.getAutor().trim());
        libro.setIsbn(isbnLimpio);
        libro.setCategoria(request.getCategoria().trim());
        libro.setStockTotal(request.getStockTotal());
        libro.setStockDisponible(request.getStockTotal() - ejemplaresPrestados);

        return mapToResponse(libroRepository.save(libro));
    }

    @Transactional
    public void eliminar(Long id) {
        Libro libro = buscarEntidadPorId(id);

        if (prestamoRepository.existsByLibroIdAndEstado(id, EstadoPrestamo.ACTIVO)) {
            throw new BusinessRuleException("No se puede eliminar el libro porque tiene préstamos activos asociados");
        }
        if (!libro.getPrestamos().isEmpty()) {
            throw new BusinessRuleException("No se puede eliminar el libro porque posee historial de préstamos registrados");
        }

        libroRepository.delete(libro);
    }

    private Libro buscarEntidadPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
    }

    private LibroResponse mapToResponse(Libro libro) {
        return new LibroResponse(
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getCategoria(),
                libro.getStockTotal(),
                libro.getStockDisponible()
        );
    }
}
