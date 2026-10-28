package com.example.mpaz_pro_6springboot.identidad.auth.dto.response;

import com.example.mpaz_pro_6springboot.identidad.usuario.dto.response.UsuarioResponse;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn,
        UsuarioResponse usuario
) {
}
