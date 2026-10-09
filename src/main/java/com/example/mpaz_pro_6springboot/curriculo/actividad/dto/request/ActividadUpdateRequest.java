package com.example.mpaz_pro_6springboot.curriculo.actividad.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActividadUpdateRequest(

        @Pattern(regexp = "(?s).*\\S.*")
        @Size(max = 200)
        String titulo,

        TipoActividad tipo,

        @Min(1)
        Integer orden
) {
}