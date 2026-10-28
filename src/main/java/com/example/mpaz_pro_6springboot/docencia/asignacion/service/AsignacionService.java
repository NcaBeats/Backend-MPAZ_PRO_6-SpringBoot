package com.example.mpaz_pro_6springboot.docencia.asignacion.service;

import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.repository.AsignacionOaRepository;
import com.example.mpaz_pro_6springboot.docencia.docencia.model.DocenteCursoAsignatura;
import com.example.mpaz_pro_6springboot.docencia.docencia.repository.DocenteCursoAsignaturaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignacionService {

    private static final int MAX_INTENTOS_PREDETERMINADO = 1;

    private final AsignacionRepository asignacionRepository;
    private final CursoRepository cursoRepository;
    private final UnidadRepository unidadRepository;
    private final AsignacionOaRepository asignacionOaRepository;
    private final AsignacionActividadRepository asignacionActividadRepository;
    private final IntentoRepository intentoRepository;
    private final DocenteCursoAsignaturaRepository docenciaRepository;

    public List<Asignacion> findAll(Long cursoId, Long unidadId) {
        if (cursoId != null) {
            getCurso(cursoId);
        }
        if (unidadId != null) {
            getUnidad(unidadId);
        }

        if (cursoId != null && unidadId != null) {
            return asignacionRepository.findByCursoIdAndUnidadIdOrderByIdAsc(
                    cursoId,
                    unidadId
            );
        }
        if (cursoId != null) {
            return asignacionRepository.findByCursoIdOrderByIdAsc(cursoId);
        }
        if (unidadId != null) {
            return asignacionRepository.findByUnidadIdOrderByIdAsc(unidadId);
        }
        return asignacionRepository.findAllByOrderByIdAsc();
    }

    public Asignacion findById(Long id) {
        return getAsignacion(id);
    }

    @Transactional
    public Asignacion create(
            Asignacion asignacion,
            Long cursoId,
            Long unidadId,
            Usuario currentUser
    ) {
        Curso curso = getCurso(cursoId);
        Unidad unidad = getUnidad(unidadId);

        DocenteCursoAsignatura docencia = docenciaRepository
                .findByCursoIdAndAsignaturaId(cursoId, unidad.getAsignatura().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No tienes docencia para ese curso y asignatura"
                ));
        if (currentUser == null
                || !docencia.getDocente().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el docente de la asignatura puede crear la asignación"
            );
        }

        if (asignacionRepository.existsByCursoIdAndUnidadId(cursoId, unidadId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una asignación para el curso y la unidad"
            );
        }

        asignacion.setCurso(curso);
        asignacion.setUnidad(unidad);
        if (asignacion.getMaxIntentos() == null) {
            asignacion.setMaxIntentos(MAX_INTENTOS_PREDETERMINADO);
        }
        if (asignacion.getPruebaFinalHabilitada() == null) {
            asignacion.setPruebaFinalHabilitada(false);
        }
        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public Asignacion update(Long id, Asignacion cambios) {
        if (cambios.getMaxIntentos() == null
                && cambios.getPruebaFinalHabilitada() == null
                && cambios.getFecha() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Asignacion asignacion = getAsignacion(id);
        if (cambios.getMaxIntentos() != null) {
            asignacion.setMaxIntentos(cambios.getMaxIntentos());
        }
        if (cambios.getPruebaFinalHabilitada() != null) {
            asignacion.setPruebaFinalHabilitada(cambios.getPruebaFinalHabilitada());
        }
        if (cambios.getFecha() != null) {
            asignacion.setFecha(cambios.getFecha());
        }
        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public void delete(Long id) {
        Asignacion asignacion = getAsignacion(id);
        if (asignacionOaRepository.existsByAsignacionId(id)
                || asignacionActividadRepository.existsByAsignacionId(id)
                || intentoRepository.existsByActividadUnidadIdAndEstudianteCursoId(
                        asignacion.getUnidad().getId(),
                        asignacion.getCurso().getId()
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una asignación que tiene objetivos, actividades o intentos asociados"
            );
        }
        asignacionRepository.delete(asignacion);
    }

    private Asignacion getAsignacion(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Asignación no encontrada"
                ));
    }

    private Curso getCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curso no encontrado"
                ));
    }

    private Unidad getUnidad(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidad no encontrada"
                ));
    }
}
