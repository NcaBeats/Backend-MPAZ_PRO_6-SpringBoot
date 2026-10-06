package com.example.mpaz_pro_6springboot.identidad.usuario.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(

        @NotBlank
        @Size(max = 150)
        String nombre,

        @NotBlank
        @Size(min = 3, max = 100)
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "solo letras, digitos, punto, guion y guion bajo")
        String username,

        /** Texto plano: el Service aplica el hash, nunca se persiste asi. */
        @NotBlank
        @Size(min = 8, max = 255)
        String password,

        @NotNull
        Rol rol,

        /** Solo para Rol.ESTUDIANTE. Lo resuelve el Service con getReferenceById. */
        Long cursoId
) {
}