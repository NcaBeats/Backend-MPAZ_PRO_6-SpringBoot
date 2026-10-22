package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de generación del informe. El Service calcula la fecha de generación,
 * el estado inicial y el detalle por OA a partir de los intentos completados.
 */
public record InformePedagogicoCreateRequest(

        @NotNull
        Long estudianteId,

        @NotNull
        Long unidadId,

        @NotNull
        Long docenteId
) {
}
