package pe.edu.zegel.biblioteca.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.zegel.biblioteca.dto.PrestamoRequest;
import pe.edu.zegel.biblioteca.dto.PrestamoResponse;
import pe.edu.zegel.biblioteca.entity.EstadoPrestamo;
import pe.edu.zegel.biblioteca.entity.Libro;
import pe.edu.zegel.biblioteca.entity.Prestamo;
import pe.edu.zegel.biblioteca.entity.Usuario;
import pe.edu.zegel.biblioteca.exception.BusinessRuleException;
import pe.edu.zegel.biblioteca.exception.ResourceNotFoundException;
import pe.edu.zegel.biblioteca.repository.LibroRepository;
import pe.edu.zegel.biblioteca.repository.PrestamoRepository;
import pe.edu.zegel.biblioteca.repository.UsuarioRepository;

/**
 * Servicio de negocio para el registro de préstamos, control de stock y devoluciones.
 */
@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(
            PrestamoRepository prestamoRepository,
            UsuarioRepository usuarioRepository,
            LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional
    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + request.getUsuarioId()));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessRuleException("El usuario se encuentra inactivo y no puede solicitar préstamos");
        }

        Libro libro = libroRepository.findById(request.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con ID: " + request.getLibroId()));

        if (libro.getStockDisponible() <= 0) {
            throw new BusinessRuleException(
                    "No hay stock disponible para el libro '" + libro.getTitulo() + "'");
        }

        // Descuenta un ejemplar del stock disponible
        libro.setStockDisponible(libro.getStockDisponible() - 1);
        libroRepository.save(libro);

        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaDevolucionEstimada(hoy.plusDays(request.getDiasPrestamo()));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo guardado = prestamoRepository.save(prestamo);
        return mapToResponse(guardado);
    }

    @Transactional
    public PrestamoResponse devolverPrestamo(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Préstamo no encontrado con ID: " + prestamoId));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessRuleException("El préstamo con ID " + prestamoId + " ya fue devuelto anteriormente");
        }

        Libro libro = prestamo.getLibro();
        int nuevoStock = Math.min(libro.getStockTotal(), libro.getStockDisponible() + 1);
        libro.setStockDisponible(nuevoStock);
        libroRepository.save(libro);

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDate.now());

        Prestamo actualizado = prestamoRepository.save(prestamo);
        return mapToResponse(actualizado);
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarTodos() {
        return prestamoRepository.findAllWithUsuarioAndLibro().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarMisPrestamos(String emailUsuario) {
        return prestamoRepository.findHistorialPorEmailUsuario(emailUsuario).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrestamoResponse mapToResponse(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getUsuario().getId(),
                prestamo.getUsuario().getNombre(),
                prestamo.getUsuario().getEmail(),
                prestamo.getLibro().getId(),
                prestamo.getLibro().getTitulo(),
                prestamo.getLibro().getIsbn(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEstimada(),
                prestamo.getFechaDevolucionReal(),
                prestamo.getEstado()
        );
    }
}
