package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Matriz de acceso por rol. Las escrituras se prueban con cuerpos invalidos:
 * si el rol no esta autorizado la respuesta es 401/403; si esta autorizado, la
 * validacion devuelve 400 o el Service 404, lo que demuestra que paso el filtro.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadRutasTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void elCatalogoSoloLoEscribeLaUtp() throws Exception {
        mockMvc.perform(post("/api/asignaturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/asignaturas")
                        .with(bearer("camila"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/asignaturas")
                        .with(bearer("docente"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/asignaturas")
                        .with(bearer("utp"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void laAutorizacionDeUnidadesNoQuedaEnManosDelDocente() throws Exception {
        mockMvc.perform(put("/api/unidades/{id}", 999999)
                        .with(bearer("docente"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"estado":"AUTORIZADO","autorizadaPorId":1,"fechaAutorizacion":"2026-01-01T00:00:00Z"}
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/unidades/{id}", 999999)
                        .with(bearer("utp"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Otra unidad"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void lasAsignacionesSoloLasEscribeElDocente() throws Exception {
        mockMvc.perform(post("/api/asignaciones")
                        .with(bearer("camila"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/asignaciones")
                        .with(bearer("docente"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/asignaciones")
                        .with(bearer("utp"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void laEvaluacionSoloLaOperaElEstudiante() throws Exception {
        mockMvc.perform(post("/api/estudiantes/{estudianteId}/actividades/{actividadId}/intentos", 3, 1))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/estudiantes/{estudianteId}/actividades/{actividadId}/intentos", 3, 1)
                        .with(bearer("docente")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/estudiantes/{estudianteId}/actividades/{actividadId}/intentos", 3, 1)
                        .with(bearer("utp")))
                .andExpect(status().isForbidden());
    }

    @Test
    void losEndpointsDeLecturaDeEvaluacionRequierenAutenticacion() throws Exception {
        mockMvc.perform(get("/api/intentos"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/intentos/{id}", 1))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/intentos/{id}/respuestas", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lasRutasPublicasSiguenAbiertas() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void elCompletarUnIntentoEsSoloParaElEstudiante() throws Exception {
        mockMvc.perform(patch("/api/intentos/{id}/completar", 1)
                        .with(bearer("docente")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/intentos/{id}/respuestas", 1)
                        .with(bearer("docente"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"preguntaId\":1,\"alternativaId\":1}"))
                .andExpect(status().isForbidden());
    }
}
