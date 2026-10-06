package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.NivelOa;
import jakarta.validation.constraints.Min;

/**
 * informe_id y oa_id forman el unico (informe_id, oa_id), no se actualizan.
 */
public record DetalleInformeOaUpdateRequest(

        @Min(0)
        Integer totalPreguntas,

        @Min(0)
        Integer correctas,

        NivelOa nivel
) {
}