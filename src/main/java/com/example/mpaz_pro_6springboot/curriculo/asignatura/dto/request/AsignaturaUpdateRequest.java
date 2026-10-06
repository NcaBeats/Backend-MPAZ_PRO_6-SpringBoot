package com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.request;

import jakarta.validation.constraints.Size;

public record AsignaturaUpdateRequest(

        @Size(max = 100)
        String nombre
) {
}