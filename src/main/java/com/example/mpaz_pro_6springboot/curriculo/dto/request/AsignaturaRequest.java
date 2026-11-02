package com.example.mpaz_pro_6springboot.curriculo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de creacion de Asignatura. Las referencias se envian como identificadores. */
public record AsignaturaRequest(
        @NotBlank @Size(max = 50) String nombre
) {
}
