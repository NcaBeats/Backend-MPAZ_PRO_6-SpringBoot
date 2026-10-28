package com.example.mpaz_pro_6springboot.identidad.auth;

import com.example.mpaz_pro_6springboot.common.PostgresIntegrationTest;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CursoRepository cursoRepository;

    @Test
    void elLoginDevuelveUnTokenYMeReconoceElToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"camila","password":"alu12345"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.usuario.username").value("camila"))
                .andExpect(jsonPath("$.usuario.rol").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.usuario.cursoId").isNumber())
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
        assertFalse(token.isBlank());

        mockMvc.perform(get("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("camila"));
    }

    @Test
    void elLoginConCredencialesInvalidasDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"camila","password":"incorrecta"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elLoginSinCuerpoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void meSinAutenticacionDevuelve401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutSinAutenticacionDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutConTokenDevuelve204() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(bearer("camila")))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void elRegistroConUtpCreaUnEstudianteYPermiteLogin() throws Exception {
        Long cursoId = cursoRepository.findAll().getFirst().getId();

        mockMvc.perform(post("/api/auth/register")
                        .with(bearer("utp"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Nuevo Alumno","username":"nuevo.alumno",
                                 "password":"secreto123","rol":"ESTUDIANTE","cursoId":%d}
                                """.formatted(cursoId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/usuarios/")))
                .andExpect(jsonPath("$.username").value("nuevo.alumno"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.cursoId").value(cursoId));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"nuevo.alumno","password":"secreto123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.username").value("nuevo.alumno"));
    }

    @Test
    @Transactional
    void elRegistroSinAutenticacionDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Sin Permiso","username":"sin.permiso",
                                 "password":"secreto123","rol":"ESTUDIANTE","cursoId":1}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void elRegistroConDocenteDevuelve403() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(bearer("docente"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Sin Permiso","username":"sin.permiso",
                                 "password":"secreto123","rol":"ESTUDIANTE","cursoId":1}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void elRegistroDeEstudianteSinCursoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(bearer("utp"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Sin Curso","username":"sin.curso",
                                 "password":"secreto123","rol":"ESTUDIANTE"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void elRegistroConUsernameDuplicadoDevuelve409() throws Exception {
        Long cursoId = cursoRepository.findAll().getFirst().getId();

        mockMvc.perform(post("/api/auth/register")
                        .with(bearer("utp"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Camila Otra","username":"camila",
                                 "password":"secreto123","rol":"ESTUDIANTE","cursoId":%d}
                                """.formatted(cursoId)))
                .andExpect(status().isConflict());
    }

    @Test
    void laGestionDeUsuariosEstaReservadaAUtp() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/usuarios")
                        .with(bearer("camila")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/usuarios")
                        .with(bearer("utp")))
                .andExpect(status().isOk());
    }

    @Test
    void lasRutasDeCatalogoYaNoSonPublicas() throws Exception {
        mockMvc.perform(get("/api/asignaturas"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/asignaturas")
                        .with(bearer("camila")))
                .andExpect(status().isOk());
    }
}
