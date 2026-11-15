package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de Unidad. */
public record UnidadResponse(
        Long id,
        String nombre,
        String descripcion,
        AsignaturaResumen asignatura,
        EstadoAutorizacionResumen estado
) {
}
