package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifica las reglas 9 y 10 sin levantar Spring ni la base de datos.
 */
class EvaluacionAccessPolicyTest {

    private final EvaluacionAccessPolicy policy = new EvaluacionAccessPolicy();

    @Test
    void elEstudianteSoloSeVeASiMismo() {
        Usuario camila = usuario(3L, Rol.ESTUDIANTE);

        assertEquals(3L, policy.scopeEstudianteId(null, camila));
        assertEquals(3L, policy.scopeEstudianteId(3L, camila));
        assertThrows(ResponseStatusException.class, () -> policy.scopeEstudianteId(4L, camila));
    }

    @Test
    void docenteYUtpConservanElFiltroSolicitado() {
        assertEquals(4L, policy.scopeEstudianteId(4L, usuario(2L, Rol.DOCENTE)));
        assertEquals(4L, policy.scopeEstudianteId(4L, usuario(1L, Rol.UTP)));
        assertNull(policy.scopeEstudianteId(null, usuario(1L, Rol.UTP)));
    }

    @Test
    void soloElEstudianteDuenoPuedeIniciar() {
        Usuario camila = usuario(3L, Rol.ESTUDIANTE);

        assertDoesNotThrow(() -> policy.assertPuedeIniciar(3L, camila));
        assertThrows(ResponseStatusException.class, () -> policy.assertPuedeIniciar(4L, camila));
        assertThrows(ResponseStatusException.class,
                () -> policy.assertPuedeIniciar(3L, usuario(2L, Rol.DOCENTE)));
    }

    @Test
    void elEstudianteSoloLeeYOperaSusIntentos() {
        Usuario camila = usuario(3L, Rol.ESTUDIANTE);
        Usuario docente = usuario(2L, Rol.DOCENTE);
        Intento propio = Intento.builder().id(10L).estudiante(camila).build();
        Intento ajeno = Intento.builder().id(11L).estudiante(usuario(4L, Rol.ESTUDIANTE)).build();

        assertDoesNotThrow(() -> policy.assertPuedeLeer(propio, camila));
        assertDoesNotThrow(() -> policy.assertPuedeOperar(propio, camila));
        assertThrows(ResponseStatusException.class, () -> policy.assertPuedeLeer(ajeno, camila));
        assertThrows(ResponseStatusException.class, () -> policy.assertPuedeOperar(ajeno, camila));
        assertDoesNotThrow(() -> policy.assertPuedeLeer(ajeno, docente));
        assertThrows(ResponseStatusException.class, () -> policy.assertPuedeOperar(ajeno, docente));
    }

    private Usuario usuario(Long id, Rol rol) {
        return Usuario.builder().id(id).rol(rol).build();
    }
}
