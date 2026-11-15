package com.example.mpaz_pro_6springboot.identidad.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** DTO de actualizacion parcial de Usuario. Todos los campos son opcionales. */
public record UsuarioUpdateRequest(
        @Size(max = 25) String pnombre,
        @Size(max = 25) String snombre,
        @Size(max = 25) String apaterno,
        @Size(max = 25) String amaterno,
        @Size(max = 50) String nombreUsuario,
        @Size(min = 8, max = 100) String password,
        @Positive Long rolId
) {

    @Override
    public String toString() {
        return "UsuarioUpdateRequest[nombreUsuario=" + nombreUsuario + ", password=***]";
    }
}
