package com.example.mpaz_pro_6springboot.curriculo.unidad.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UnidadCreateRequest(

        @NotNull
        Long asignaturaId,

        @Size(max = 50)
        String nivelEducativo,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @Min(1)
        Integer orden,

        @NotNull
        EstadoContenido estado
) {
}