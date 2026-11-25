package pe.edu.zegel.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.zegel.biblioteca.security.JwtAccessDeniedHandler;
import pe.edu.zegel.biblioteca.security.JwtAuthenticationEntryPoint;
import pe.edu.zegel.biblioteca.security.JwtAuthenticationFilter;

/**
 * Configuración central de seguridad HTTP basada en JWT sin estado (STATELESS)
 * y control de acceso basado en roles (RBAC).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            JwtAccessDeniedHandler accessDeniedHandler,
            UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Permite que la consola web H2 cargue sus frames en el navegador
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de autenticación
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
                        // Rutas públicas de catálogo de libros (solo lectura)
                        .requestMatchers(HttpMethod.GET, "/api/libros", "/api/libros/**").permitAll()
                        // Swagger / OpenAPI y Consola H2
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/h2-console/**").permitAll()

                        // Ruta accesible para USUARIO y ADMIN (debe declararse antes de /api/prestamos)
                        .requestMatchers(HttpMethod.GET, "/api/prestamos/mis-prestamos").hasAnyRole("USUARIO", "ADMIN")

                        // Rutas exclusivas de ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/libros").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/libros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/libros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/prestamos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/prestamos/*/devolver").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/prestamos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios", "/api/usuarios/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
