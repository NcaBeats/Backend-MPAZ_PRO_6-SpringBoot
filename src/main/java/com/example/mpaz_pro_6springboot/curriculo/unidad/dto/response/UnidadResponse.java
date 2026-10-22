package com.example.mpaz_pro_6springboot.curriculo.unidad.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;

import java.time.Instant;

public record UnidadResponse(
        Long id,
        Long asignaturaId,
        String nivelEducativo,
        String titulo,
        Integer orden,
        EstadoContenido estado,
        Long autorizadaPorId,
        Instant fechaAutorizacion
) {
}