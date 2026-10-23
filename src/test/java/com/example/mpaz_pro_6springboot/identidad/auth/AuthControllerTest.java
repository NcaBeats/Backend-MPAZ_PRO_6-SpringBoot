package com.example.mpaz_pro_6springboot.identidad.auth;

import com.example.mpaz_pro_6springboot.common.PostgresIntegrationTest;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
    void elLoginConBasicDevuelveElUsuarioSinSesionNiToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("camila"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.cursoId").isNumber())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andReturn();

        assertNull(result.getRequest().getSession(false), "Basic stateless no debe crear sesion");
    }

    @Test
    void elLoginSinCredencialesDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elLoginConCredencialesInvalidasDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(httpBasic("camila", "incorrecta")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meConBasicDevuelveElUsuario() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("camila"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"));
    }

    @Test
    void meSinAutenticacionDevuelve401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void elRegistroConUtpCreaUnEstudianteYPermiteLogin() throws Exception {
        Long cursoId = cursoRepository.findAll().getFirst().getId();

        mockMvc.perform(post("/api/auth/register")
                        .with(httpBasic("utp", "utp12345"))
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
                        .with(httpBasic("nuevo.alumno", "secreto123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nuevo.alumno"));
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
                        .with(httpBasic("docente", "doc12345"))
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
                        .with(httpBasic("utp", "utp12345"))
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
                        .with(httpBasic("utp", "utp12345"))
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
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/usuarios")
                        .with(httpBasic("utp", "utp12345")))
                .andExpect(status().isOk());
    }

    @Test
    void lasRutasPublicasSiguenSinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/asignaturas"))
                .andExpect(status().isOk());
    }
}
