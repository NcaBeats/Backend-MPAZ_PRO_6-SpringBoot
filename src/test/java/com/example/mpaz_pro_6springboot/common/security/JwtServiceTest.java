package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.identidad.model.Rol;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "test-secret-0123456789abcdef-test-secret",
            3600
    );

    @Test
    void generaYParseaUnTokenConLosClaimsDelUsuario() {
        Usuario usuario = Usuario.builder()
                .id(6L)
                .nombreUsuario("alumno")
                .passwordHash("hash")
                .rol(Rol.builder().id(3L).nombre("ESTUDIANTE").build())
                .activo(true)
                .build();

        String token = jwtService.generateToken(new CustomUserDetails(usuario));
        Optional<JwtClaims> parsed = jwtService.parse(token);

        assertTrue(parsed.isPresent());
        assertEquals("alumno", parsed.get().username());
        assertEquals(6L, parsed.get().usuarioId());
        assertEquals("ESTUDIANTE", parsed.get().rol());
        assertTrue(parsed.get().expiration() > Instant.now().getEpochSecond());
    }

    @Test
    void rechazaUnTokenManipulado() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombreUsuario("utp")
                .passwordHash("hash")
                .rol(Rol.builder().id(1L).nombre("UTP").build())
                .activo(true)
                .build();

        String token = jwtService.generateToken(new CustomUserDetails(usuario));
        String manipulado = token.substring(0, token.length() - 1) + "x";

        assertFalse(jwtService.parse(manipulado).isPresent());
    }

    @Test
    void rechazaUnTokenVencido() {
        JwtService expirado = new JwtService("test-secret-0123456789abcdef", -1);
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombreUsuario("utp")
                .passwordHash("hash")
                .rol(Rol.builder().id(1L).nombre("UTP").build())
                .activo(true)
                .build();

        String token = expirado.generateToken(new CustomUserDetails(usuario));

        assertFalse(expirado.parse(token).isPresent());
    }
}
