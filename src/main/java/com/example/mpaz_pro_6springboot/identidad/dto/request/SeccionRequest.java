package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de creacion de Seccion. Las referencias se envian como identificadores. */
public record SeccionRequest(
        @NotBlank @Size(max = 1) String letra
) {
}
