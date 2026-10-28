package com.example.mpaz_pro_6springboot.common.seed;

import com.example.mpaz_pro_6springboot.common.PostgresIntegrationTest;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.repository.RecursoContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.repository.AsignacionOaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.model.RespuestaEstudiante;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.repository.RespuestaEstudianteRepository;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model.DetalleInformeOa;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.repository.DetalleInformeOaRepository;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository.InformePedagogicoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que el esquema PostgreSQL + el DataInitializer dejen los datos de demo
 * consistentes, incluidas las garantias que aporta la base (FK simples y
 * compuestas, CHECK e indice parcial) y el snapshot del informe.
 */
@SpringBootTest
class DataInitializerTest extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AsignaturaRepository asignaturaRepository;
    @Autowired
    private UnidadRepository unidadRepository;
    @Autowired
    private ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    @Autowired
    private ContenidoRepository contenidoRepository;
    @Autowired
    private RecursoContenidoRepository recursoContenidoRepository;
    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private PreguntaRepository preguntaRepository;
    @Autowired
    private AlternativaRepository alternativaRepository;
    @Autowired
    private AsignacionRepository asignacionRepository;
    @Autowired
    private AsignacionOaRepository asignacionOaRepository;
    @Autowired
    private AsignacionActividadRepository asignacionActividadRepository;
    @Autowired
    private IntentoRepository intentoRepository;
    @Autowired
    private RespuestaEstudianteRepository respuestaEstudianteRepository;
    @Autowired
    private InformePedagogicoRepository informePedagogicoRepository;
    @Autowired
    private DetalleInformeOaRepository detalleInformeOaRepository;

    @Test
    void elSeedCargaTodosLosDatos() {
        assertEquals(1, cursoRepository.count());
        assertEquals(6, usuarioRepository.count());
        assertEquals(4, asignaturaRepository.count());
        assertEquals(4, unidadRepository.count());
        assertEquals(8, objetivoAprendizajeRepository.count());
        assertEquals(8, contenidoRepository.count());
        assertEquals(8, recursoContenidoRepository.count());
        assertEquals(12, actividadRepository.count());
        assertEquals(6, preguntaRepository.count());
        assertEquals(19, alternativaRepository.count());
        assertEquals(4, asignacionRepository.count());
        assertEquals(8, asignacionOaRepository.count());
        assertEquals(12, asignacionActividadRepository.count());
        assertEquals(4, intentoRepository.count());
        assertEquals(12, respuestaEstudianteRepository.count());
        assertEquals(1, informePedagogicoRepository.count());
        assertEquals(2, detalleInformeOaRepository.count());

        assertTrue(asignacionRepository.findAllByOrderByIdAsc().stream()
                .allMatch(a -> Boolean.TRUE.equals(a.getPruebaFinalHabilitada())));
    }

    @Test
    void lasContrasenasDelSeedSonVerificables() {
        Usuario utp = usuarioRepository.findByUsername("utp").orElseThrow();
        Usuario docente = usuarioRepository.findByUsername("docente").orElseThrow();
        Usuario camila = usuarioRepository.findByUsername("camila").orElseThrow();

        assertTrue(passwordEncoder.matches("utp12345", utp.getPasswordHash()));
        assertTrue(passwordEncoder.matches("doc12345", docente.getPasswordHash()));
        assertTrue(passwordEncoder.matches("alu12345", camila.getPasswordHash()));
    }

    @Test
    void lasClavesForaneasImpidenReferenciasInexistentes() {
        // rol ESTUDIANTE + curso_id no nulo pasa el CHECK, pero el curso no
        // existe: la FK debe rechazar la fila.
        assertThrows(DataAccessException.class, () -> jdbcTemplate.update(
                "INSERT INTO usuario (nombre, username, password_hash, rol, curso_id) "
                        + "VALUES (?, ?, ?, 'ESTUDIANTE', ?)",
                "Curso inexistente", "curso.inexistente", "hash", 999_999L));
    }

    @Test
    @Transactional
    void losInstantesSobrevivenElRoundTrip() {
        Asignacion asignacion = asignacionRepository.findAllByOrderByIdAsc().get(0);

        assertEquals(Instant.parse("2026-04-06T08:00:00Z"), asignacion.getFecha());
        assertEquals(2, asignacion.getMaxIntentos());

        Intento intento = intentoRepository.findAllByOrderByEstudianteIdAscActividadIdAscNumeroAsc().get(0);
        assertNotNull(intento.getFechaInicio());
        assertNotNull(intento.getFechaFin());
    }

    @Test
    void elIndiceParcialImpideUnaSegundaPruebaFinal() {
        Long unidadId = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc().get(0).getId();

        assertThrows(DataAccessException.class, () -> jdbcTemplate.update(
                "INSERT INTO actividad (unidad_id, titulo, tipo, orden) VALUES (?, ?, 'PRUEBA_FINAL', 4)",
                unidadId, "Segunda prueba final de la unidad 1"));
    }

    @Test
    void lasFkCompuestasImpidenCruzarUnidades() {
        Long actividadUnidad1 = actividadRepository
                .findPruebaFinalByUnidadId(unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc()
                        .get(0).getId())
                .orElseThrow()
                .getId();
        Long unidad2 = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc().get(1).getId();
        Long oaUnidad2 = objetivoAprendizajeRepository.findByUnidadId(unidad2).get(0).getId();

        // (actividad_id, unidad_id) apunta a actividad(id, unidad_id): la mezcla
        // de unidad 1 con unidad 2 debe ser rechazada por la FK compuesta.
        assertThrows(DataAccessException.class, () -> jdbcTemplate.update(
                "INSERT INTO pregunta (actividad_id, unidad_id, oa_id, enunciado, dificultad, orden) "
                        + "VALUES (?, ?, ?, 'Pregunta cruzada', 'BAJA', 1)",
                actividadUnidad1, unidad2, oaUnidad2));
    }

    @Test
    @Transactional
    void elSnapshotDelInformeCoincideConElRecalculo() {
        Usuario camila = usuarioRepository.findByUsername("camila").orElseThrow();
        Unidad unidad = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc().get(0);
        Actividad pruebaFinal = actividadRepository.findPruebaFinalByUnidadId(unidad.getId()).orElseThrow();
        InformePedagogico informe = informePedagogicoRepository
                .findByEstudianteIdAndUnidadId(camila.getId(), unidad.getId())
                .orElseThrow();
        Intento ultimo = intentoRepository
                .findFirstByEstudianteIdAndActividadIdAndFechaFinIsNotNullOrderByNumeroDesc(camila.getId(), pruebaFinal.getId())
                .orElseThrow();

        Map<Long, int[]> contadores = new LinkedHashMap<>();
        for (RespuestaEstudiante respuesta : respuestaEstudianteRepository
                .findByIntentoIdOrderByIdAsc(ultimo.getId())) {
            Long oaId = respuesta.getPregunta().getOa().getId();
            int[] valores = contadores.computeIfAbsent(oaId, k -> new int[2]);
            valores[0]++;
            if (Boolean.TRUE.equals(respuesta.getEsCorrecta())) {
                valores[1]++;
            }
        }

        assertEquals(contadores.size(), detalleInformeOaRepository.findByInformeId(informe.getId()).size());

        for (DetalleInformeOa detalle : detalleInformeOaRepository.findByInformeId(informe.getId())) {
            int[] valores = contadores.get(detalle.getOa().getId());
            assertNotNull(valores, "El detalle debe corresponder a un OA con respuestas");
            assertEquals(valores[0], detalle.getTotalPreguntas());
            assertEquals(valores[1], detalle.getCorrectas());
        }
    }
}
