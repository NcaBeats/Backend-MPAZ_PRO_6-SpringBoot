package com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlternativaCreateRequest(

        @NotNull
        Long preguntaId,

        @NotBlank
        @Size(max = 500)
        String texto,

        /** Opcional: si se omite el Service aplica false. */
        Boolean esCorrecta
) {
}