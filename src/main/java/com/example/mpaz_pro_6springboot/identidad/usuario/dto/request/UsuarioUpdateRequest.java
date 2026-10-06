package com.example.mpaz_pro_6springboot.identidad.usuario.dto.request;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(

        @Size(max = 150)
        String nombre,

        @Size(min = 3, max = 100)
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "solo letras, digitos, punto, guion y guion bajo")
        String username,

        @Size(min = 8, max = 255)
        String password,

        Rol rol,

        Long cursoId
) {
}