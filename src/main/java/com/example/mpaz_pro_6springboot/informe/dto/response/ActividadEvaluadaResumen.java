package com.example.mpaz_pro_6springboot.informe.dto.response;

import java.time.Instant;

/**
 * Actividad considerada en el calculo preliminar, con el intento que se tomo
 * como ultimo para esa actividad.
 */
public record ActividadEvaluadaResumen(
        Long actividadCursoId,
        Long actividadId,
        String instrucciones,
        Long intentoId,
        Instant fechaFin
) {
}
