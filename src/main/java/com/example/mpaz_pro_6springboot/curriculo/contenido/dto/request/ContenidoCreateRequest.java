package com.example.mpaz_pro_6springboot.curriculo.contenido.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.OrigenMaterial;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContenidoCreateRequest(

        @NotNull
        Long oaId,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotBlank
        String explicacion,

        String ejemplos,

        String instrucciones,

        @Min(1)
        Integer orden,

        @NotNull
        OrigenMaterial origen
) {
}