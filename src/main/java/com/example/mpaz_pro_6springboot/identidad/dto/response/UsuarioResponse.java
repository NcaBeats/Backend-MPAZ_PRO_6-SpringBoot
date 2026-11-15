package com.example.mpaz_pro_6springboot.identidad.dto.response;

/** DTO de respuesta de Usuario. */
public record UsuarioResponse(
        Long id,
        String pnombre,
        String snombre,
        String apaterno,
        String amaterno,
        String nombreUsuario,
        RolResumen rol,
        boolean activo
) {
}
