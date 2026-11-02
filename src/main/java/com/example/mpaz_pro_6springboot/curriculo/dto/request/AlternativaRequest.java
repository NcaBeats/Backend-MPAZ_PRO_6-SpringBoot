package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** DTO de creacion de Alternativa. Las referencias se envian como identificadores. */
public record AlternativaRequest(
        String enunciado,
        @NotNull @Positive Long recursoId,
        boolean correcta
) {
}
