package com.example.mpaz_pro_6springboot.curriculo.actividad.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;

public record ActividadResponse(
        Long id,
        Long unidadId,
        // NULL = actividad transversal de la unidad (sin contenido).
        Long contenidoId,
        String titulo,
        TipoActividad tipo,
        Integer orden
) {
}
