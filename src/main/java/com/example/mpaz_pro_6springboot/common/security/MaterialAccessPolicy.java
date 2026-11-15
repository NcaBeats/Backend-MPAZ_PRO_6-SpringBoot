package com.example.mpaz_pro_6springboot.common.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.docencia.repository.AsignaturaCursoRepository;

/**
 * Reglas de escritura del material pedagogico.
 *
 * <p>UTP puede gestionar cualquier asignatura. Un docente solo puede crear o
 * modificar material de las asignaturas que tiene asignadas en
 * {@code asignatura_curso}.</p>
 */
@Component
@Transactional(readOnly = true)
public class MaterialAccessPolicy {

    private final AsignaturaCursoRepository asignaturaCursoRepository;

    public MaterialAccessPolicy(AsignaturaCursoRepository asignaturaCursoRepository) {
        this.asignaturaCursoRepository = asignaturaCursoRepository;
    }

    public void requirePuedeGestionarUnidad(Unidad unidad) {
        if (unidad == null || unidad.getAsignatura() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La unidad no tiene asignatura");
        }
        requirePuedeGestionarAsignatura(unidad.getAsignatura().getId());
    }

    public void requirePuedeGestionarContenido(Contenido contenido) {
        if (contenido == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El contenido es obligatorio");
        }
        requirePuedeGestionarUnidad(contenido.getUnidad());
    }

    public void requirePuedeGestionarActividad(Actividad actividad) {
        if (actividad == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La actividad es obligatoria");
        }
        requirePuedeGestionarContenido(actividad.getContenido());
    }

    public void requirePuedeGestionarRecurso(Recurso recurso) {
        if (recurso == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El recurso es obligatorio");
        }
        requirePuedeGestionarContenido(recurso.getContenido());
    }

    public void requirePuedeGestionarAsignatura(Long asignaturaId) {
        if (SecurityUtils.isUtp()) {
            return;
        }
        if (!SecurityUtils.isDocente()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para gestionar material pedagogico");
        }
        Long docenteId = SecurityUtils.requireUsuario().getId();
        boolean asignada = asignaturaCursoRepository.findAllByDocenteId(docenteId).stream()
                .anyMatch(asignaturaCurso -> asignaturaCurso.getAsignatura() != null
                        && asignaturaId.equals(asignaturaCurso.getAsignatura().getId()));
        if (!asignada) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes la asignatura a cargo para gestionar este material");
        }
    }
}
