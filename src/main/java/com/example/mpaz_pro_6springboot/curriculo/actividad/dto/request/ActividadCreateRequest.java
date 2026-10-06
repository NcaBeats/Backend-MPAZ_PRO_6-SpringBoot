package com.example.mpaz_pro_6springboot.curriculo.actividad.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActividadCreateRequest(

        @NotNull
        Long unidadId,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotNull
        TipoActividad tipo,

        @Min(1)
        Integer orden
) {
}