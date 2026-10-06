package com.example.mpaz_pro_6springboot.curriculo.contenido.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.OrigenMaterial;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ContenidoUpdateRequest(

        @Size(max = 200)
        String titulo,

        String explicacion,

        String ejemplos,

        String instrucciones,

        @Min(1)
        Integer orden,

        OrigenMaterial origen
) {
}