package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** DTO de creacion de Usuario. Las referencias se envian como identificadores. */
public record UsuarioRequest(
        @Size(max = 25) String pnombre,
        @Size(max = 25) String snombre,
        @Size(max = 25) String apaterno,
        @Size(max = 25) String amaterno,
        @NotBlank @Size(max = 50) String nombreUsuario,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull @Positive Long rolId,
        boolean activo
) {

    @Override
    public String toString() {
        return "UsuarioRequest[nombreUsuario=" + nombreUsuario + ", password=***]";
    }
}
