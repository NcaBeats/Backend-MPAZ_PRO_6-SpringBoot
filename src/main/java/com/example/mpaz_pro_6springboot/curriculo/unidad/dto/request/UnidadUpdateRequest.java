package com.example.mpaz_pro_6springboot.curriculo.unidad.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UnidadUpdateRequest(

        @Size(max = 50)
        String nivelEducativo,

        @Size(max = 200)
        String titulo,

        @Min(1)
        Integer orden,

        EstadoContenido estado,

        /** Exigido por el CHECK de la seccion 6 cuando estado es AUTORIZADO o PUBLICADO. */
        Long autorizadaPorId,

        Instant fechaAutorizacion
) {
}