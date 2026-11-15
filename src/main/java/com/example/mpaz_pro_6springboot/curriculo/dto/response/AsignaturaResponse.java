package com.example.mpaz_pro_6springboot.curriculo.dto.response;

import com.example.mpaz_pro_6springboot.identidad.dto.response.NivelResumen;

/** DTO de respuesta de Asignatura. */
public record AsignaturaResponse(
        Long id,
        String nombre,
        NivelResumen nivel
) {
}
