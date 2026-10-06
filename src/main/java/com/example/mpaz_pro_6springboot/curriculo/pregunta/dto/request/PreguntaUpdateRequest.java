package com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.Dificultad;
import jakarta.validation.constraints.Min;

public record PreguntaUpdateRequest(

        String enunciado,

        String criterioRespuesta,

        Dificultad dificultad,

        @Min(1)
        Integer orden
) {
}