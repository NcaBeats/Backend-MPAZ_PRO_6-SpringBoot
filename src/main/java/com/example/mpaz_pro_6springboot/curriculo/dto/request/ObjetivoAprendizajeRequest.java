package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de creacion de ObjetivoAprendizaje. Las referencias se envian como identificadores. */
public record ObjetivoAprendizajeRequest(
        @NotBlank String descripcion,
        @NotBlank @Size(max = 25) String codigo
) {
}
