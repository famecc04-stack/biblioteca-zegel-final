package pe.edu.zegel.biblioteca.dto;

import java.util.List;

/**
 * DTO de respuesta con los datos públicos de un usuario y sus roles asignados.
 */
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private Boolean activo;
    private List<String> roles;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String nombre, String email, Boolean activo, List<String> roles) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
