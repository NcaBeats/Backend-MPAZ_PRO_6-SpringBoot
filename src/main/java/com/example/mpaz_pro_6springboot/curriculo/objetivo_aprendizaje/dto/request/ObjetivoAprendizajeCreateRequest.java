package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ObjetivoAprendizajeCreateRequest(

        @NotNull
        Long unidadId,

        @Size(max = 50)
        String codigo,

        @NotBlank
        String descripcion,

        @NotBlank
        @Size(max = 100)
        String eje,

        @Size(max = 500)
        String textoReferencia
) {
}