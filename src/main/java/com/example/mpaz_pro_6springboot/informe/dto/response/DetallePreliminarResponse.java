package com.example.mpaz_pro_6springboot.informe.dto.response;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeResumen;

/** Resultado preliminar de un objetivo de aprendizaje. */
public record DetallePreliminarResponse(
        ObjetivoAprendizajeResumen objetivoAprendizaje,
        int correctas,
        int incorrectas,
        int total,
        double porcentaje
) {
}
