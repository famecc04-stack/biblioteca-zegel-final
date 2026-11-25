package pe.edu.zegel.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de solicitud para renovar el Access Token mediante un Refresh Token válido.
 */
public class RefreshTokenRequest {

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;

    public RefreshTokenRequest() {
    }

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
