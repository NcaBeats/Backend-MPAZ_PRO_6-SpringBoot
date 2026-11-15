package com.example.mpaz_pro_6springboot.docencia.dto.response;

import java.time.Instant;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.ActividadResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.CursoResumen;

/** DTO de respuesta de ActividadCurso. */
public record ActividadCursoResponse(
        Long id,
        ActividadResumen actividad,
        CursoResumen curso,
        Instant fechaInicio,
        Instant fechaFin,
        boolean habilitada,
        boolean visible,
        Integer maxIntentos
) {
}
