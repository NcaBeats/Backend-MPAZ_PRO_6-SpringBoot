package com.example.mpaz_pro_6springboot.curriculo.pregunta.service;

import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.repository.RespuestaEstudianteRepository;
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

    public List<Pregunta> findAll(Long actividadId, Long oaId) {
        if (actividadId != null) {
            getActividad(actividadId);
        }
        if (oaId != null) {
            getObjetivoAprendizaje(oaId);
        }

        if (actividadId != null && oaId != null) {
            return preguntaRepository.findByActividadIdAndOaIdOrderByOrdenAscIdAsc(actividadId, oaId);
        }
        if (actividadId != null) {
            return preguntaRepository.findByActividadIdOrderByOrdenAsc(actividadId);
        }
        if (oaId != null) {
            return preguntaRepository.findByOaIdOrderByOrdenAscIdAsc(oaId);
        }
        return preguntaRepository.findAllByOrderByActividadIdAscOrdenAscIdAsc();
    }

    public Pregunta findById(Long id) {
        return getPregunta(id);
    }

    @Transactional
    public Pregunta create(Pregunta pregunta, Long actividadId, Long unidadId, Long oaId) {
        Actividad actividad = getActividad(actividadId);
        ObjetivoAprendizaje oa = getObjetivoAprendizaje(oaId);
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
