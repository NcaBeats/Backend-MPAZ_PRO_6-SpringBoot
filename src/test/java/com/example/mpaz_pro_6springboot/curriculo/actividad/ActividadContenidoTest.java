package com.example.mpaz_pro_6springboot.curriculo.actividad;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.actividad.service.ActividadService;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.contenido.service.ContenidoService;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la asociacion opcional actividad -> contenido (V3):
 * la actividad puede quedar transversal, agruparse por contenido de su unidad
 * y la base rechaza un contenido de otra unidad por FK compuesta.
 */
@SpringBootTest
@Transactional
class ActividadContenidoTest {

    @Autowired
    private ActividadService actividadService;
    @Autowired
    private ContenidoService contenidoService;
    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private ContenidoRepository contenidoRepository;
    @Autowired
    private UnidadRepository unidadRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long unidad1;
    private Long unidad2;
    private Long contenidoUnidad1;
    private Long contenidoUnidad2;
    private Long actividadUnidad1;

    @BeforeEach
    void setUp() {
        List<Unidad> unidades = unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc();
        unidad1 = unidades.get(0).getId();
        unidad2 = unidades.get(1).getId();

        // V2 carga un contenido por OA: 1 y 2 son de la unidad 1; 3 y 4 de la 2.
        List<Contenido> contenidos = contenidoRepository.findAllByOrderByOaIdAscOrdenAscIdAsc();
        contenidoUnidad1 = contenidos.get(0).getId();
        contenidoUnidad2 = contenidos.get(2).getId();

        actividadUnidad1 = actividadRepository.findAllByOrderByUnidadIdAscOrdenAscIdAsc().get(0).getId();
    }

    @Test
    void laActividadPuedeQuedarSinContenido() {
        Actividad creada = actividadService.create(nuevaActividad("Actividad transversal"), unidad1, null);

        assertNull(creada.getContenido());
        assertEquals(unidad1, creada.getUnidad().getId());
    }

    @Test
    void asociaLaActividadAUnContenidoDeSuUnidad() {
        Actividad creada = actividadService.create(
                nuevaActividad("Actividad con contenido"), unidad1, contenidoUnidad1);

        assertEquals(contenidoUnidad1, creada.getContenido().getId());
        assertEquals(unidad1, creada.getContenido().getUnidad().getId());
    }

    @Test
    void rechazaUnContenidoDeOtraUnidad() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                actividadService.create(nuevaActividad("Actividad cruzada"), unidad1, contenidoUnidad2));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void desasociaElContenidoParaVolverlaTransversal() {
        Actividad creada = actividadService.create(
                nuevaActividad("Actividad que luego es transversal"), unidad1, contenidoUnidad1);
        assertEquals(contenidoUnidad1, creada.getContenido().getId());

        Actividad actualizada = actividadService.update(creada.getId(), Actividad.builder().build(), null, true);

        assertNull(actualizada.getContenido());
    }

    @Test
    void noSePuedeAsignarYDesasociarEnLaMismaPeticion() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                actividadService.update(actividadUnidad1, Actividad.builder().build(), contenidoUnidad1, true));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void filtraPorContenidoYPorActividadesTransversales() {
        List<Actividad> delContenido = actividadService.findAll(unidad1, contenidoUnidad1, false);
        assertFalse(delContenido.isEmpty());
        assertTrue(delContenido.stream()
                .allMatch(a -> a.getContenido() != null && a.getContenido().getId().equals(contenidoUnidad1)));

        List<Actividad> transversales = actividadService.findAll(unidad1, null, true);
        assertFalse(transversales.isEmpty());
        assertTrue(transversales.stream().allMatch(a -> a.getContenido() == null));

        // La prueba final de la unidad 1 es transversal por definicion.
        assertTrue(transversales.stream()
                .anyMatch(a -> a.getTipo() == TipoActividad.PRUEBA_FINAL));
    }

    @Test
    void losFiltrosDeContenidoYTransversalesSonExcluyentes() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                actividadService.findAll(unidad1, contenidoUnidad1, true));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void laFkCompuestaImpideLigarUnContenidoDeOtraUnidad() {
        // Se salta el Service a proposito: la garantia tiene que estar en la base.
        assertThrows(DataAccessException.class, () -> jdbcTemplate.update(
                "UPDATE actividad SET contenido_id = ? WHERE id = ?",
                contenidoUnidad2, actividadUnidad1));
    }

    @Test
    void noSePuedeEliminarUnContenidoConActividades() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                contenidoService.delete(contenidoUnidad1));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    private Actividad nuevaActividad(String titulo) {
        return Actividad.builder()
                .titulo(titulo)
                .tipo(TipoActividad.ACTIVIDAD)
                .orden(9)
                .build();
    }
}
