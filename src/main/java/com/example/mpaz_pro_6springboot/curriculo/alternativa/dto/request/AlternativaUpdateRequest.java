package com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.request;

import jakarta.validation.constraints.Size;

public record AlternativaUpdateRequest(

        @Size(max = 500)
        String texto,

        Boolean esCorrecta
) {
}