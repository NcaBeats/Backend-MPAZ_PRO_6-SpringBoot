package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de PreguntaActividad. */
public record PreguntaActividadResponse(
        Long id,
        ActividadResumen actividad,
        PreguntaResumen pregunta
) {
}
