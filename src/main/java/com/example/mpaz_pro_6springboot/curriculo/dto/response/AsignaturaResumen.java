package com.example.mpaz_pro_6springboot.curriculo.dto.response;

import com.example.mpaz_pro_6springboot.identidad.dto.response.NivelResumen;

/** Resumen de Asignatura usado en respuestas anidadas. */
public record AsignaturaResumen(
        Long id,
        String nombre,
        NivelResumen nivel
) {
}
