package com.example.mpaz_pro_6springboot.curriculo.dto.response;

import java.time.Instant;

/** DTO de respuesta de Actividad. */
public record ActividadResponse(
        Long id,
        String instrucciones,
        TipoActividadResumen tipoActividad,
        ContenidoResumen contenido,
        Instant fechaInicio,
        Instant fechaTermino,
        boolean habilitada,
        Integer maxIntentos,
        EstadoAutorizacionResumen estado
) {
}
