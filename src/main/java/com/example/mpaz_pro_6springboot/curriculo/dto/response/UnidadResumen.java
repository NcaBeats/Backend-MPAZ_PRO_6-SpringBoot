package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** Resumen de Unidad usado en respuestas anidadas. */
public record UnidadResumen(
        Long id,
        String nombre,
        AsignaturaResumen asignatura
) {
}
