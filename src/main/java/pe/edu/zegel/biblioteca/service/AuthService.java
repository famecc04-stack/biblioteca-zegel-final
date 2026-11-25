package pe.edu.zegel.biblioteca.service;

import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.zegel.biblioteca.dto.AuthResponse;
import pe.edu.zegel.biblioteca.dto.LoginRequest;
import pe.edu.zegel.biblioteca.dto.RefreshTokenRequest;
import pe.edu.zegel.biblioteca.dto.RegisterRequest;
import pe.edu.zegel.biblioteca.entity.Rol;
import pe.edu.zegel.biblioteca.entity.Usuario;
import pe.edu.zegel.biblioteca.exception.BusinessRuleException;
import pe.edu.zegel.biblioteca.exception.InvalidTokenException;
import pe.edu.zegel.biblioteca.exception.ResourceNotFoundException;
import pe.edu.zegel.biblioteca.repository.RolRepository;
import pe.edu.zegel.biblioteca.repository.UsuarioRepository;
import pe.edu.zegel.biblioteca.security.JwtService;

/**
 * Servicio encargado del registro de usuarios, inicio de sesión y renovación de tokens JWT.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String emailNormalizado = request.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new BusinessRuleException("Ya existe un usuario registrado con el correo: " + emailNormalizado);
        }

        Rol rolUsuario = rolRepository.findByNombre("ROLE_USUARIO")
                .orElseThrow(() -> new ResourceNotFoundException("El rol ROLE_USUARIO no se encuentra configurado"));

        Usuario nuevoUsuario = new Usuario(
                request.getNombre().trim(),
                emailNormalizado,
                passwordEncoder.encode(request.getPassword()),
                true
        );
        nuevoUsuario.setRoles(Set.of(rolUsuario));
        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(guardado.getEmail());
        return construirAuthResponse(guardado, userDetails);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String emailNormalizado = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailNormalizado, request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + emailNormalizado));

        return construirAuthResponse(usuario, userDetails);
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken().trim();
        String userEmail = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new InvalidTokenException("El refresh token es inválido, expiró o no corresponde al tipo REFRESH");
        }

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userEmail));

        return construirAuthResponse(usuario, userDetails);
    }

    private AuthResponse construirAuthResponse(Usuario usuario, UserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getJwtExpirationMs(),
                usuarioService.mapToResponse(usuario)
        );
    }
}
