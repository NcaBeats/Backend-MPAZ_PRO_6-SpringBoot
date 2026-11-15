package com.example.mpaz_pro_6springboot.identidad.dto.response;

/** Resumen de Curso usado en respuestas anidadas. */
public record CursoResumen(
        Long id,
        Integer annio,
        NivelResumen nivel,
        SeccionResumen seccion
) {
}
