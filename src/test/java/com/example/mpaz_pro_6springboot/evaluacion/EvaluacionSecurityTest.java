package com.example.mpaz_pro_6springboot.evaluacion;

import com.example.mpaz_pro_6springboot.common.PostgresIntegrationTest;
import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Reglas 9 y 10 de la seccion 8: un estudiante solo consulta y modifica sus
 * propios intentos y respuestas; UTP y DOCENTE solo leen.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EvaluacionSecurityTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private IntentoRepository intentoRepository;
    @Autowired
    private UnidadRepository unidadRepository;
    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private PreguntaRepository preguntaRepository;
    @Autowired
    private AlternativaRepository alternativaRepository;

    @Test
    void elEstudianteSoloVeSusPropiosIntentos() throws Exception {
        Usuario camila = usuario("camila");
        Usuario mateo = usuario("mateo");
        Long pruebaFinalId = pruebaFinalUnidadUno().getId();

        mockMvc.perform(get("/api/intentos")
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estudianteId").value(camila.getId()))
                .andExpect(jsonPath("$[1].estudianteId").value(camila.getId()));

        mockMvc.perform(get("/api/intentos")
                        .param("actividadId", pruebaFinalId.toString())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estudianteId").value(camila.getId()))
                .andExpect(jsonPath("$[1].estudianteId").value(camila.getId()));

        mockMvc.perform(get("/api/intentos")
                        .param("estudianteId", mateo.getId().toString())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/intentos")
                        .with(httpBasic("docente", "doc12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));

        mockMvc.perform(get("/api/intentos")
                        .with(httpBasic("utp", "utp12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void elEstudianteNoPuedeVerElIntentoDeOtro() throws Exception {
        Usuario mateo = usuario("mateo");
        Intento intentoMateo = intentoDe("mateo");

        mockMvc.perform(get("/api/intentos/{id}", intentoMateo.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/intentos/{id}", intentoMateo.getId())
                        .with(httpBasic("docente", "doc12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudianteId").value(mateo.getId()));

        mockMvc.perform(get("/api/intentos/{id}", intentoMateo.getId())
                        .with(httpBasic("utp", "utp12345")))
                .andExpect(status().isOk());
    }

    @Test
    void elEstudianteSoloIniciaSusPropiosIntentos() throws Exception {
        Usuario camila = usuario("camila");
        Usuario mateo = usuario("mateo");
        Actividad actividad = actividadSinIntentos();

        mockMvc.perform(post("/api/estudiantes/{estudianteId}/actividades/{actividadId}/intentos",
                        mateo.getId(), actividad.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/estudiantes/{estudianteId}/actividades/{actividadId}/intentos",
                        camila.getId(), actividad.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estudianteId").value(camila.getId()))
                .andExpect(jsonPath("$.numero").value(1));
    }

    @Test
    void elEstudianteNoPuedeCompletarElIntentoDeOtro() throws Exception {
        Intento intentoJaviera = intentoDe("javiera");

        mockMvc.perform(patch("/api/intentos/{id}/completar", intentoJaviera.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/intentos/{id}/completar", intentoJaviera.getId())
                        .with(httpBasic("javiera", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaFin").isNotEmpty());
    }

    @Test
    void elEstudianteNoPuedeResponderEnElIntentoDeOtro() throws Exception {
        Intento intentoJaviera = intentoDe("javiera");
        Pregunta pregunta = preguntaRepository
                .findByActividadIdOrderByOrdenAsc(intentoJaviera.getActividad().getId())
                .getFirst();
        Alternativa alternativa = alternativaRepository.findByPreguntaId(pregunta.getId()).getFirst();
        String cuerpo = """
                {"preguntaId":%d,"alternativaId":%d}
                """.formatted(pregunta.getId(), alternativa.getId());

        mockMvc.perform(post("/api/intentos/{id}/respuestas", intentoJaviera.getId())
                        .with(httpBasic("camila", "alu12345"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/intentos/{id}/respuestas", intentoJaviera.getId())
                        .with(httpBasic("docente", "doc12345"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/intentos/{id}/respuestas", intentoJaviera.getId())
                        .with(httpBasic("javiera", "alu12345"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.intentoId").value(intentoJaviera.getId()))
                .andExpect(jsonPath("$.preguntaId").value(pregunta.getId()));
    }

    @Test
    void elEstudianteSoloVeSusPropiasRespuestas() throws Exception {
        Intento intentoCamila = intentoMasReciente("camila");
        Intento intentoMateo = intentoDe("mateo");

        mockMvc.perform(get("/api/intentos/{id}/respuestas", intentoCamila.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));

        mockMvc.perform(get("/api/intentos/{id}/respuestas", intentoMateo.getId())
                        .with(httpBasic("camila", "alu12345")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/intentos/{id}/respuestas", intentoMateo.getId())
                        .with(httpBasic("docente", "doc12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    private Usuario usuario(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow();
    }

    private Intento intentoDe(String username) {
        return intentoRepository
                .findByEstudianteIdOrderByActividadIdAscNumeroAsc(usuario(username).getId())
                .getFirst();
    }

    private Intento intentoMasReciente(String username) {
        return intentoRepository
                .findByEstudianteIdOrderByActividadIdAscNumeroAsc(usuario(username).getId())
                .getLast();
    }

    private Actividad pruebaFinalUnidadUno() {
        Long unidadId = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc().getFirst().getId();
        return actividadRepository.findPruebaFinalByUnidadId(unidadId).orElseThrow();
    }

    /**
     * La prueba final de la unidad 1 ya tiene intentos de Camila; una actividad
     * normal de la misma unidad no, y no aplica el limite de intentos.
     */
    private Actividad actividadSinIntentos() {
        Long unidadId = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc().getFirst().getId();
        return actividadRepository.findByUnidadIdAndTipo(unidadId, TipoActividad.ACTIVIDAD).getFirst();
    }
}
