package com.example.mpaz_pro_6springboot.identidad.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Credenciales de acceso.
 */
public record LoginRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(max = 255) String password
) {
}
