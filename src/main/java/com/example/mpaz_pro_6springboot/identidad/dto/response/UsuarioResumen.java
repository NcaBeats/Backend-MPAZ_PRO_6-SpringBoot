package com.example.mpaz_pro_6springboot.identidad.dto.response;

/** Resumen de Usuario usado en respuestas anidadas. */
public record UsuarioResumen(
        Long id,
        String nombreUsuario,
        String nombreCompleto
) {
}
