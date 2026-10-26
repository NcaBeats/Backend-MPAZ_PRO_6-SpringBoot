package com.example.mpaz_pro_6springboot.curriculo.actividad.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Todos los campos son opcionales. Como null significa "no cambiar", para
 * quitar el contenido se usa desasociarContenido=true (con contenidoId null).
 */
public record ActividadUpdateRequest(

        Long contenidoId,

        Boolean desasociarContenido,

        @Pattern(regexp = "(?s).*\\S.*")
        @Size(max = 200)
        String titulo,

        TipoActividad tipo,

        @Min(1)
        Integer orden
) {
}
