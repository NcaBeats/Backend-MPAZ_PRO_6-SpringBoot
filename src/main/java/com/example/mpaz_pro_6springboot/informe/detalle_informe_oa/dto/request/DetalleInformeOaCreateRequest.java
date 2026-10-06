package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.NivelOa;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetalleInformeOaCreateRequest(

        @NotNull
        Long informeId,

        @NotNull
        Long oaId,

        @NotNull
        @Min(0)
        Integer totalPreguntas,

        @NotNull
        @Min(0)
        Integer correctas,

        @NotNull
        NivelOa nivel
) {
}