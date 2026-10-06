package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.response;

import com.example.mpaz_pro_6springboot.common.enums.EstadoInforme;

import java.time.Instant;

public record InformePedagogicoResponse(
        Long id,
        Long estudianteId,
        Long unidadId,
        Long docenteId,
        Instant fechaGeneracion,
        EstadoInforme estado,
        Instant fechaValidacion
) {
}