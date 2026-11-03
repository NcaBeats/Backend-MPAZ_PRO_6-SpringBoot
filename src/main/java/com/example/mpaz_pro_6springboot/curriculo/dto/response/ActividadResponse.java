package com.example.mpaz_pro_6springboot.curriculo.dto.response;

/** DTO de respuesta de Actividad. */
public record ActividadResponse(
        Long id,
        String instrucciones,
        TipoActividadResumen tipoActividad,
        ContenidoResumen contenido,
        EstadoAutorizacionResumen estado
) {
}
