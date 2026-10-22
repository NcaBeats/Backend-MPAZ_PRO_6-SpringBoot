package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.response;

public record ObjetivoAprendizajeResponse(
        Long id,
        Long unidadId,
        String codigo,
        String descripcion,
        String eje,
        String textoReferencia
) {
}