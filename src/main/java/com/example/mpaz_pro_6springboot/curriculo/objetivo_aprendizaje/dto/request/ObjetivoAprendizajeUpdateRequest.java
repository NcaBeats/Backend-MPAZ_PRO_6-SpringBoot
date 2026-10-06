package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.request;

import jakarta.validation.constraints.Size;

public record ObjetivoAprendizajeUpdateRequest(

        @Size(max = 50)
        String codigo,

        String descripcion,

        @Size(max = 100)
        String eje,

        @Size(max = 500)
        String textoReferencia
) {
}