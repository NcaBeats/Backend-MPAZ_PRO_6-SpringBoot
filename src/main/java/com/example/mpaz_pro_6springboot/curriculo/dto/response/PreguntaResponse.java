package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de Pregunta. */
public record PreguntaResponse(
        Long id,
        String enunciado,
        RecursoResumen recurso
) {
}
