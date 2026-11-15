package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de creacion de Nivel. Las referencias se envian como identificadores. */
public record NivelRequest(
        @NotBlank @Size(max = 25) String nombre
) {
}
