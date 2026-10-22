package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "test-secret-with-at-least-32-bytes-0123456789";

    private final JwtService jwtService = new JwtService(SECRET, 3600);

    @Test
    void generaYValidaElToken() {
        String token = jwtService.generateToken(details());

        JwtClaims claims = jwtService.parse(token).orElseThrow();
        assertEquals("camila", claims.username());
        assertEquals(7L, claims.usuarioId());
        assertEquals("ESTUDIANTE", claims.rol());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void rechazaUnTokenManipulado() {
        String token = jwtService.generateToken(details());
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertTrue(jwtService.parse(tampered).isEmpty());
    }

    @Test
    void rechazaUnTokenFirmadoConOtroSecreto() {
        String token = new JwtService("otro-secreto-de-al-menos-32-bytes-abcdef", 3600)
                .generateToken(details());

        assertTrue(jwtService.parse(token).isEmpty());
    }

    @Test
    void rechazaUnTokenExpirado() {
        String token = new JwtService(SECRET, -1).generateToken(details());

        assertFalse(jwtService.parse(token).isPresent());
    }

    private static CustomUserDetails details() {
        Usuario usuario = Usuario.builder()
                .id(7L)
                .nombre("Camila Rivas")
                .username("camila")
                .passwordHash("hash")
                .rol(Rol.ESTUDIANTE)
                .build();
        return new CustomUserDetails(usuario);
    }
}
