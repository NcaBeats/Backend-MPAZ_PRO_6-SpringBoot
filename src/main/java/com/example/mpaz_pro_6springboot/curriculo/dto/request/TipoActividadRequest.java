package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de creacion de TipoActividad. Las referencias se envian como identificadores. */
public record TipoActividadRequest(
        @NotBlank @Size(max = 50) String nombre
) {
}
