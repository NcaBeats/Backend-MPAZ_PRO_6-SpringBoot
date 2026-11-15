package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de creacion de Rol. Las referencias se envian como identificadores. */
public record RolRequest(
        @NotBlank @Size(max = 25) String nombre
) {
}
