package com.example.mpaz_pro_6springboot.curriculo.pregunta.service;

import com.example.mpaz_pro_6springboot.common.security.CurriculoAuthoringPolicy;
import com.example.mpaz_pro_6springboot.common.security.StudentVisibilityService;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.repository.RespuestaEstudianteRepository;
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
public class PreguntaService {

    private static final int ORDEN_PREDETERMINADO = 1;

    private final PreguntaRepository preguntaRepository;
    private final ActividadRepository actividadRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final AlternativaRepository alternativaRepository;
    private final RespuestaEstudianteRepository respuestaEstudianteRepository;
    private final CurriculoAuthoringPolicy authoringPolicy;
    private final StudentVisibilityService visibilityService;

    public List<Pregunta> findAll(Long actividadId, Long oaId, Usuario currentUser) {
        if (actividadId != null) {
            getActividad(actividadId);
            if (!visibilityService.canSeeActivity(currentUser, actividadId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La actividad no está asignada al curso del estudiante"
                );
            }
        }
        if (oaId != null) {
            getObjetivoAprendizaje(oaId);
        }

        List<Pregunta> preguntas;
        if (actividadId != null && oaId != null) {
            preguntas = preguntaRepository.findByActividadIdAndOaIdOrderByOrdenAscIdAsc(
                    actividadId,
                    oaId
            );
        } else if (actividadId != null) {
            preguntas = preguntaRepository.findByActividadIdOrderByOrdenAsc(actividadId);
        } else if (oaId != null) {
            preguntas = preguntaRepository.findByOaIdOrderByOrdenAscIdAsc(oaId);
        } else {
            preguntas = preguntaRepository.findAllByOrderByActividadIdAscOrdenAscIdAsc();
        }

        if (!visibilityService.isStudent(currentUser)) {
            return preguntas;
        }

        var visibles = visibilityService.visibleActivityIds(currentUser);
        return preguntas.stream()
                .filter(pregunta -> visibles.contains(pregunta.getActividad().getId()))
                .toList();
    }

    public Pregunta findById(Long id, Usuario currentUser) {
        Pregunta pregunta = getPregunta(id);
        if (!visibilityService.canSeeActivity(currentUser, pregunta.getActividad())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La actividad de la pregunta no está asignada al curso del estudiante"
            );
        }
        return pregunta;
    }

    @Transactional
    public Pregunta create(
            Pregunta pregunta,
            Long actividadId,
            Long unidadId,
            Long oaId,
            Usuario currentUser
    ) {
        Actividad actividad = getActividad(actividadId);
        ObjetivoAprendizaje oa = getObjetivoAprendizaje(oaId);
        authoringPolicy.assertCanAuthor(currentUser, actividad.getUnidad());
        Long unidadActividad = actividad.getUnidad().getId();

        if (!unidadActividad.equals(unidadId) || !unidadActividad.equals(oa.getUnidad().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La actividad, la unidad y el objetivo de aprendizaje deben pertenecer a la misma unidad"
            );
        }
        if (pregunta.getOrden() == null) {
            pregunta.setOrden(ORDEN_PREDETERMINADO);
        }

        pregunta.setActividad(actividad);
        pregunta.setUnidad(actividad.getUnidad());
        pregunta.setOa(oa);
        return preguntaRepository.save(pregunta);
    }

    @Transactional
    public Pregunta update(Long id, Pregunta cambios) {
        if (isEmptyUpdate(cambios)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Pregunta pregunta = getPregunta(id);
        if (cambios.getEnunciado() != null) {
            pregunta.setEnunciado(cambios.getEnunciado());
        }
        if (cambios.getCriterioRespuesta() != null) {
            pregunta.setCriterioRespuesta(cambios.getCriterioRespuesta());
        }
        if (cambios.getDificultad() != null) {
            pregunta.setDificultad(cambios.getDificultad());
        }
        if (cambios.getOrden() != null) {
            pregunta.setOrden(cambios.getOrden());
        }
        return preguntaRepository.save(pregunta);
    }

    @Transactional
    public void delete(Long id) {
        Pregunta pregunta = getPregunta(id);
        if (alternativaRepository.existsByPreguntaId(id)
                || respuestaEstudianteRepository.existsByPreguntaId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una pregunta que tiene alternativas o respuestas de estudiantes asociadas"
            );
        }
        preguntaRepository.delete(pregunta);
    }

    private boolean isEmptyUpdate(Pregunta cambios) {
        return cambios.getEnunciado() == null
                && cambios.getCriterioRespuesta() == null
                && cambios.getDificultad() == null
                && cambios.getOrden() == null;
    }

    private Pregunta getPregunta(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
    }

    private Actividad getActividad(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }

    private ObjetivoAprendizaje getObjetivoAprendizaje(Long id) {
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Objetivo de aprendizaje no encontrado"
                ));
    }
}
