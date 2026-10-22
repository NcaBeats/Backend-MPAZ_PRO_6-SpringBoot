package com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AsignaturaCreateRequest(

        @NotBlank
        @Size(max = 100)
        String nombre
) {
}