package com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.Dificultad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PreguntaCreateRequest(

        @NotNull
        Long actividadId,

        /** Se repite porque la FK compuesta (actividad_id, unidad_id) la exige. */
        @NotNull
        Long unidadId,

        /** Se repite porque la FK compuesta (oa_id, unidad_id) la exige. */
        @NotNull
        Long oaId,

        @NotBlank
        String enunciado,

        String criterioRespuesta,

        @NotNull
        Dificultad dificultad,

        @Min(1)
        Integer orden
) {
}