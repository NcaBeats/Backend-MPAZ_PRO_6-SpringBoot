package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.EstadoInforme;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record InformePedagogicoCreateRequest(

        @NotNull
        Long estudianteId,

        @NotNull
        Long unidadId,

        @NotNull
        Long docenteId,

        @NotNull
        Instant fechaGeneracion,

        /** Si se omite el Service aplica GENERADO. */
        EstadoInforme estado,

        Instant fechaValidacion
) {
}