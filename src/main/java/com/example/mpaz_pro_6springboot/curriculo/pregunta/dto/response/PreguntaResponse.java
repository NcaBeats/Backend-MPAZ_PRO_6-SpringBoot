package com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.Dificultad;

public record PreguntaResponse(
        Long id,
        Long actividadId,
        Long unidadId,
        Long oaId,
        String enunciado,
        String criterioRespuesta,
        Dificultad dificultad,
        Integer orden
) {
}