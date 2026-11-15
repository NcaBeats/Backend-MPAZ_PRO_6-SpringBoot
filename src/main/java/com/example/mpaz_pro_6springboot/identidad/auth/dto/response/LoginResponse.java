package com.example.mpaz_pro_6springboot.identidad.auth.dto.response;

import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResponse;

/**
 * Respuesta del login: token JWT y datos publicos del usuario.
 */
public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn,
        UsuarioResponse usuario
) {
}
