package com.example.mpaz_pro_6springboot.docencia.asignacion.service;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
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

    public List<Asignacion> findAll(
            Long cursoId,
            Long unidadId,
            Usuario currentUser
    ) {
        if (cursoId != null) {
            getCurso(cursoId);
        }
        if (unidadId != null) {
            getUnidad(unidadId);
        }

        List<Asignacion> asignaciones;
        if (cursoId != null && unidadId != null) {
            asignaciones = asignacionRepository.findByCursoIdAndUnidadIdOrderByIdAsc(
                    cursoId,
                    unidadId
            );
        } else if (cursoId != null) {
            asignaciones = asignacionRepository.findByCursoIdOrderByIdAsc(cursoId);
        } else if (unidadId != null) {
            asignaciones = asignacionRepository.findByUnidadIdOrderByIdAsc(unidadId);
        } else {
            asignaciones = asignacionRepository.findAllByOrderByIdAsc();
        }

        return filtrarVisibles(asignaciones, currentUser);
    }

    public Asignacion findById(Long id, Usuario currentUser) {
        Asignacion asignacion = getAsignacion(id);
        assertPuedeLeer(asignacion, currentUser);
        return asignacion;
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
    public Asignacion update(Long id, Asignacion cambios, Usuario currentUser) {
        if (cambios.getMaxIntentos() == null
                && cambios.getPruebaFinalHabilitada() == null
                && cambios.getFecha() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Asignacion asignacion = getAsignacion(id);
        assertDocenteDueno(asignacion, currentUser);

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
    public void delete(Long id, Usuario currentUser) {
        Asignacion asignacion = getAsignacion(id);
        assertDocenteDueno(asignacion, currentUser);

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

    /** Valida que el docente autenticado sea el dueño de la docencia de la asignación. */
    public void assertDocenteDueno(Asignacion asignacion, Usuario currentUser) {
        if (currentUser == null || currentUser.getRol() != Rol.DOCENTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el docente de la asignatura puede modificarla"
            );
        }

        DocenteCursoAsignatura docencia = docenciaRepository
                .findByCursoIdAndAsignaturaId(
                        asignacion.getCurso().getId(),
                        asignacion.getUnidad().getAsignatura().getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No tienes docencia para esa asignación"
                ));
        if (!docencia.getDocente().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el docente de la asignatura puede modificarla"
            );
        }
    }

    private List<Asignacion> filtrarVisibles(
            List<Asignacion> asignaciones,
            Usuario currentUser
    ) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autenticado");
        }
        if (currentUser.getRol() == Rol.UTP) {
            return asignaciones;
        }
        if (currentUser.getRol() == Rol.DOCENTE) {
            return asignaciones.stream()
                    .filter(a -> esDocenteDe(a, currentUser))
                    .toList();
        }
        if (currentUser.getRol() == Rol.ESTUDIANTE) {
            Long cursoId = currentUser.getCurso() == null
                    ? null
                    : currentUser.getCurso().getId();
            return asignaciones.stream()
                    .filter(a -> cursoId != null
                            && cursoId.equals(a.getCurso().getId()))
                    .toList();
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
    }

    private void assertPuedeLeer(Asignacion asignacion, Usuario currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autenticado");
        }
        if (currentUser.getRol() == Rol.UTP) {
            return;
        }
        if (currentUser.getRol() == Rol.DOCENTE && esDocenteDe(asignacion, currentUser)) {
            return;
        }
        if (currentUser.getRol() == Rol.ESTUDIANTE
                && currentUser.getCurso() != null
                && currentUser.getCurso().getId().equals(asignacion.getCurso().getId())) {
            return;
        }
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No puedes consultar una asignación ajena"
        );
    }

    private boolean esDocenteDe(Asignacion asignacion, Usuario currentUser) {
        return docenciaRepository
                .findByCursoIdAndAsignaturaId(
                        asignacion.getCurso().getId(),
                        asignacion.getUnidad().getAsignatura().getId()
                )
                .map(d -> d.getDocente().getId().equals(currentUser.getId()))
                .orElse(false);
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
