package com.example.mpaz_pro_6springboot.docencia.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.model.ActividadCurso;
import com.example.mpaz_pro_6springboot.docencia.repository.ActividadCursoRepository;
import com.example.mpaz_pro_6springboot.docencia.repository.AsignaturaCursoRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link ActividadCurso}.
 *
 * <p>Publica una actividad (plantilla) en un curso con su propia ventana,
 * habilitacion, visibilidad y limite de intentos. La asignacion exige que el
 * curso imparta la asignatura de la actividad.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActividadCursoService {

    private final ActividadCursoRepository actividadCursoRepository;
    private final ActividadRepository actividadRepository;
    private final CursoRepository cursoRepository;
    private final AsignaturaCursoRepository asignaturaCursoRepository;

    public List<ActividadCurso> listar() {
        return actividadCursoRepository.findAll();
    }

    public List<ActividadCurso> listarPorCurso(Long cursoId) {
        return actividadCursoRepository.findAllByCursoId(cursoId);
    }

    public List<ActividadCurso> listarPorActividad(Long actividadId) {
        return actividadCursoRepository.findAllByActividadId(actividadId);
    }

    public ActividadCurso obtener(Long id) {
        return actividadCursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("ActividadCurso", id));
    }

    @Transactional
    public ActividadCurso crear(ActividadCurso datos) {
        Validaciones.requerido(datos, "actividadCurso");

        Actividad actividad = resolverActividad(datos.getActividad());
        Curso curso = resolverCurso(datos.getCurso());

        if (actividadCursoRepository.existsByActividadIdAndCursoId(
                actividad.getId(), curso.getId())) {
            throw new ConflictoException(
                    "La actividad ya esta asignada al curso indicado");
        }
        validarAsignaturaDelCurso(actividad, curso);
        validarFechas(datos.getFechaInicio(), datos.getFechaFin());
        validarMaxIntentos(datos.getMaxIntentos());

        datos.setId(null);
        datos.setActividad(actividad);
        datos.setCurso(curso);
        return actividadCursoRepository.save(datos);
    }

    /**
     * Actualiza la ventana y el limite de intentos. Los flags
     * {@code habilitada} y {@code visible} se cambian con
     * {@link #cambiarHabilitada(Long, boolean)} y
     * {@link #cambiarVisible(Long, boolean)}.
     */
    @Transactional
    public ActividadCurso actualizar(Long id, ActividadCurso datos) {
        Validaciones.requerido(datos, "actividadCurso");
        ActividadCurso asignacion = obtener(id);

        if (datos.getFechaInicio() != null) {
            asignacion.setFechaInicio(datos.getFechaInicio());
        }
        if (datos.getFechaFin() != null) {
            asignacion.setFechaFin(datos.getFechaFin());
        }
        if (datos.getMaxIntentos() != null) {
            validarMaxIntentos(datos.getMaxIntentos());
            asignacion.setMaxIntentos(datos.getMaxIntentos());
        }

        validarFechas(asignacion.getFechaInicio(), asignacion.getFechaFin());
        return asignacion;
    }

    @Transactional
    public ActividadCurso cambiarHabilitada(Long id, boolean habilitada) {
        ActividadCurso asignacion = obtener(id);
        asignacion.setHabilitada(habilitada);
        return asignacion;
    }

    @Transactional
    public ActividadCurso cambiarVisible(Long id, boolean visible) {
        ActividadCurso asignacion = obtener(id);
        asignacion.setVisible(visible);
        return asignacion;
    }

    @Transactional
    public void eliminar(Long id) {
        actividadCursoRepository.delete(obtener(id));
    }

    /**
     * Regla de negocio: el curso debe tener registrada la asignatura de la
     * actividad en {@code asignatura_curso}. El esquema no puede expresarlo
     * porque la asignatura se obtiene a traves de contenido -> unidad.
     */
    private void validarAsignaturaDelCurso(Actividad actividad, Curso curso) {
        Long asignaturaId = actividad.getContenido().getUnidad().getAsignatura().getId();
        if (!asignaturaCursoRepository.existsByCursoIdAndAsignaturaId(
                curso.getId(), asignaturaId)) {
            throw new ReglaNegocioException(
                    "El curso no imparte la asignatura de la actividad");
        }
    }

    private void validarFechas(Instant inicio, Instant fin) {
        if (inicio != null && fin != null && !fin.isAfter(inicio)) {
            throw new ReglaNegocioException(
                    "La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }

    private void validarMaxIntentos(Integer maxIntentos) {
        if (maxIntentos != null && maxIntentos <= 0) {
            throw new ReglaNegocioException("maxIntentos debe ser mayor que cero");
        }
    }

    private Actividad resolverActividad(Actividad actividad) {
        if (actividad == null || actividad.getId() == null) {
            throw new ReglaNegocioException("El campo actividad es obligatorio");
        }
        return actividadRepository.findById(actividad.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Actividad", actividad.getId()));
    }

    private Curso resolverCurso(Curso curso) {
        if (curso == null || curso.getId() == null) {
            throw new ReglaNegocioException("El campo curso es obligatorio");
        }
        return cursoRepository.findById(curso.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", curso.getId()));
    }
}
