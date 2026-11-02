package com.example.mpaz_pro_6springboot.identidad.dto.response;

/** DTO de respuesta de Curso. */
public record CursoResponse(
        Long id,
        Integer annio,
        NivelResumen nivel,
        SeccionResumen seccion
) {
}
