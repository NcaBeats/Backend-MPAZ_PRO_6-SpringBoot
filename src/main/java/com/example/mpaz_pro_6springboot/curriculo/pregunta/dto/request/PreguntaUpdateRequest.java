package com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.Dificultad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record PreguntaUpdateRequest(

        @Pattern(regexp = "(?s).*\\S.*")
        String enunciado,

        String criterioRespuesta,

        Dificultad dificultad,

        @Min(1)
        Integer orden
) {
}