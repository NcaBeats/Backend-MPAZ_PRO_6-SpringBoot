package com.example.mpaz_pro_6springboot.common;

import com.jayway.jsonpath.JsonPath;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verificacion de contrato contra PostgreSQL embebido.
 *
 * <p>Se usa cuando Docker no esta disponible; el esquema, el seed y los
 * endpoints son los mismos que corren contra PostgreSQL 17.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class MobileContractIntegrationTest {

    private static final EmbeddedPostgres POSTGRES = startPostgres();

    @Autowired
    private MockMvc mockMvc;

    private static EmbeddedPostgres startPostgres() {
        try {
            return EmbeddedPostgres.start();
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> POSTGRES.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("mpaz.auth.jwt.secret",
                () -> "test-secret-0123456789abcdef-test-secret");
    }

    @AfterAll
    static void stopPostgres() throws IOException {
        POSTGRES.close();
    }

    @Test
    void elSeedYElLoginJwtFuncionanConElContratoDelMobile() throws Exception {
        String tokenUtp = login("utp");
        mockMvc.perform(get("/api/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUtp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(9)))
                .andExpect(jsonPath("$[0].nombreUsuario").exists())
                .andExpect(jsonPath("$[0].rol.nombre").exists());

        String tokenDocente = login("docente");
        mockMvc.perform(get("/api/asignaturas-curso")
                        .param("docenteId", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDocente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));

        String tokenAlumno = login("alumno");
        mockMvc.perform(get("/api/unidades")
                        .param("asignaturaId", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAlumno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado.nombre").value("AUTORIZADO"));

        mockMvc.perform(get("/api/actividades-curso")
                        .param("cursoId", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAlumno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));

        mockMvc.perform(get("/api/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAlumno))
                .andExpect(status().isForbidden());
    }

    @Test
    void elEstudiantePuedeIniciarResponderYCerrarUnIntento() throws Exception {
        String token = login("alumno");

        MvcResult creado = mockMvc.perform(post("/api/intentos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuarioId":6,"actividadCursoId":3,"fechaInicio":"2026-11-14T10:00:00Z"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        Number intentoId = JsonPath.read(
                creado.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/respuestas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"intentoId":%d,"preguntaId":1,"alternativaId":1}
                                """.formatted(intentoId.longValue())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pregunta.id").value(1))
                .andExpect(jsonPath("$.alternativa.id").value(1));

        mockMvc.perform(post("/api/intentos/{id}/cerrar", intentoId.longValue())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaFin").exists());
    }

    @Test
    void elDocentePuedeCrearContenidoActividadYPreguntas() throws Exception {
        String token = login("docente");

        MvcResult contenidoResult = mockMvc.perform(post("/api/contenidos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descripcion":"Contenido creado por el contrato mobile","unidadId":1,"estadoId":4}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long contenidoId = ((Number) JsonPath.read(
                contenidoResult.getResponse().getContentAsString(), "$.id")).longValue();

        MvcResult recursoResult = mockMvc.perform(post("/api/recursos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Material de la actividad","url":null,"contenidoId":%d,"tipoRecursoId":3}
                                """.formatted(contenidoId)))
                .andExpect(status().isCreated())
                .andReturn();
        long recursoId = ((Number) JsonPath.read(
                recursoResult.getResponse().getContentAsString(), "$.id")).longValue();

        MvcResult actividadResult = mockMvc.perform(post("/api/actividades")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instrucciones":"Actividad de prueba","contenidoId":%d,"tipoActividadId":1,"estadoId":4}
                                """.formatted(contenidoId)))
                .andExpect(status().isCreated())
                .andReturn();
        long actividadId = ((Number) JsonPath.read(
                actividadResult.getResponse().getContentAsString(), "$.id")).longValue();

        MvcResult preguntaResult = mockMvc.perform(post("/api/preguntas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"enunciado":"¿Pregunta de prueba?","recursoId":%d}
                                """.formatted(recursoId)))
                .andExpect(status().isCreated())
                .andReturn();
        long preguntaId = ((Number) JsonPath.read(
                preguntaResult.getResponse().getContentAsString(), "$.id")).longValue();

        MvcResult alternativaResult = mockMvc.perform(post("/api/alternativas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"enunciado":"Alternativa correcta","correcta":true,"recursoId":%d}
                                """.formatted(recursoId)))
                .andExpect(status().isCreated())
                .andReturn();
        long alternativaId = ((Number) JsonPath.read(
                alternativaResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(post("/api/preguntas-actividad")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"actividadId":%d,"preguntaId":%d}
                                """.formatted(actividadId, preguntaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/alternativas-pregunta")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"preguntaId":%d,"alternativaId":%d}
                                """.formatted(preguntaId, alternativaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/objetivos-aprendizaje-contenido")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contenidoId":%d,"objetivoAprendizajeId":1,"unidadId":1}
                                """.formatted(contenidoId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/actividades-curso")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"actividadId":%d,"cursoId":1,"habilitada":true,"visible":true,"maxIntentos":3}
                                """.formatted(actividadId)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult actividadCursoResult = mockMvc.perform(post("/api/actividades-curso")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"actividadId":%d,"cursoId":2,"habilitada":true,"visible":true,"maxIntentos":3}
                                """.formatted(actividadId)))
                .andExpect(status().isCreated())
                .andReturn();
        long actividadCursoId = ((Number) JsonPath.read(
                actividadCursoResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/actividades/{id}", actividadId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instrucciones":"Instrucciones editadas"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instrucciones").value("Instrucciones editadas"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/preguntas/{id}", preguntaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"enunciado":"¿Pregunta editada?","recursoId":%d}
                                """.formatted(recursoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enunciado").value("¿Pregunta editada?"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/alternativas/{id}", alternativaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"enunciado":"Alternativa editada","recursoId":%d}
                                """.formatted(recursoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enunciado").value("Alternativa editada"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/alternativas/{id}/correcta", alternativaId)
                        .param("correcta", "true")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correcta").value(true));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/actividades-curso/{id}", actividadCursoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"maxIntentos":5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxIntentos").value(5));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/actividades-curso/{id}/habilitada", actividadCursoId)
                        .param("habilitada", "false")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitada").value(false));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/actividades-curso/{id}/visible", actividadCursoId)
                        .param("visible", "false")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visible").value(false));

        mockMvc.perform(post("/api/informes-pedagogicos/desde-resultados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId":6,
                                  "unidadId":2,
                                  "fecha":"2026-11-14T00:00:00Z",
                                  "descripcion":"Informe de contrato",
                                  "fechaCorte":"2026-11-14T00:00:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.informe.id").isNumber())
                .andExpect(jsonPath("$.informe.usuario.nombreUsuario").value("alumno"))
                .andExpect(jsonPath("$.detalles").isArray());
    }

    @Test
    void laUtpPuedeCrearUsuarioAsignarDocenteYCambiarEstadoDeUnidad() throws Exception {
        String token = login("utp");

        MvcResult usuarioResult = mockMvc.perform(post("/api/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreUsuario":"nuevo_docente",
                                  "password":"mpaz123",
                                  "pnombre":"Nuevo",
                                  "snombre":null,
                                  "apaterno":"Docente",
                                  "amaterno":null,
                                  "rolId":2,
                                  "activo":true
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long docenteId = ((Number) JsonPath.read(
                usuarioResult.getResponse().getContentAsString(), "$.id")).longValue();

        MvcResult asignaturaResult = mockMvc.perform(post("/api/asignaturas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Arte","nivelId":1}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long asignaturaId = ((Number) JsonPath.read(
                asignaturaResult.getResponse().getContentAsString(), "$.id")).longValue();

        MvcResult docenciaResult = mockMvc.perform(post("/api/asignaturas-curso")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cursoId":1,"asignaturaId":%d,"docenteId":%d}
                                """.formatted(asignaturaId, docenteId)))
                .andExpect(status().isCreated())
                .andReturn();
        long docenciaId = ((Number) JsonPath.read(
                docenciaResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/asignaturas-curso/{id}", docenciaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"docenteId":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.docente.id").value(2));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/unidades/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"estadoId":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado.nombre").value("AUTORIZADO"));

        String tokenDocenteSinCursos = login("nuevo_docente");
        mockMvc.perform(get("/api/informes-pedagogicos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDocenteSinCursos))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/informes-pedagogicos/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDocenteSinCursos))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/informes-pedagogicos/preliminar")
                        .param("usuarioId", "6")
                        .param("unidadId", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDocenteSinCursos))
                .andExpect(status().isForbidden());
    }

    @Test
    void elContenidoLargoDelSeedSeAplica() throws Exception {
        String token = login("alumno");

        MvcResult result = mockMvc.perform(get("/api/contenidos")
                        .param("unidadId", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andReturn();

        String descripcion = JsonPath.read(
                result.getResponse().getContentAsString(), "$[0].descripcion");
        assertTrue(descripcion.length() > 5000,
                "El contenido de matemática debe tener el Markdown largo, pero tiene "
                        + descripcion.length() + " caracteres");
    }

    private String login(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"mpaz123"}
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.usuario.nombreUsuario").value(username))
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }
}
