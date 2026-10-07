package com.example.mpaz_pro_6springboot.identidad.curso.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CursoUpdateRequest(

        @NotBlank
        @Size(max = 50)
        String nombre,

        @Min(2000)
        @Max(2100)
        Integer anio
) {
}