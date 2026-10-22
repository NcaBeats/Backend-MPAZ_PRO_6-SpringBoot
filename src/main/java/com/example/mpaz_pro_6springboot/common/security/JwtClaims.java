package com.example.mpaz_pro_6springboot.common.security;

public record JwtClaims(
        String username,
        Long usuarioId,
        String rol,
        long expiresAt
) {
}
