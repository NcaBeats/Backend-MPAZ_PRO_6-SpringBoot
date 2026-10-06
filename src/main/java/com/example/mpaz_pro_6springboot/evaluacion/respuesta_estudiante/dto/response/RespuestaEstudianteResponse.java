package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.response;

public record RespuestaEstudianteResponse(
        Long id,
        Long intentoId,
        Long preguntaId,
        Long alternativaId,
        Boolean esCorrecta
) {
}