package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.NivelOa;

public record DetalleInformeOaResponse(
        Long id,
        Long informeId,
        Long oaId,
        Integer totalPreguntas,
        Integer correctas,
        NivelOa nivel
) {
}