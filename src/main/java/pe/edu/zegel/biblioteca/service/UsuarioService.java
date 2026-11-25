package pe.edu.zegel.biblioteca.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.zegel.biblioteca.dto.UsuarioResponse;
import pe.edu.zegel.biblioteca.entity.Rol;
import pe.edu.zegel.biblioteca.entity.Usuario;
import pe.edu.zegel.biblioteca.exception.ResourceNotFoundException;
import pe.edu.zegel.biblioteca.repository.UsuarioRepository;

/**
 * Servicio de negocio para la consulta y mapeo de usuarios del sistema.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAllWithRoles().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToResponse(usuario);
    }

    public UsuarioResponse mapToResponse(Usuario usuario) {
        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .sorted()
                .collect(Collectors.toList());

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getActivo(),
                roles
        );
    }
}
