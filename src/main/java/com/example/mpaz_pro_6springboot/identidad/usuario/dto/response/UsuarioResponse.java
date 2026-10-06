package com.example.mpaz_pro_6springboot.identidad.usuario.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.Rol;

import java.time.Instant;

public record UsuarioResponse(
        Long id,
        String nombre,
        String username,
        Rol rol,
        Long cursoId,
        Instant createdAt
) {
}