package com.example.mpaz_pro_6springboot.common.security;

/**
 * Datos minimos que viajan dentro de un JWT emitido por el backend.
 */
public record JwtClaims(
        String username,
        Long usuarioId,
        String rol,
        long expiration
) {
}
