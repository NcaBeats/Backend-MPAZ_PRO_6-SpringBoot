package com.example.mpaz_pro_6springboot.curriculo.alternativa.service;

import com.example.mpaz_pro_6springboot.common.security.CurriculoAuthoringPolicy;
import com.example.mpaz_pro_6springboot.common.security.StudentVisibilityService;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.repository.AlternativaRepository;
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
public class AlternativaService {

    private final AlternativaRepository alternativaRepository;
    private final PreguntaRepository preguntaRepository;
    private final RespuestaEstudianteRepository respuestaEstudianteRepository;
    private final CurriculoAuthoringPolicy authoringPolicy;
    private final StudentVisibilityService visibilityService;

    public List<Alternativa> findAll(Long preguntaId, Usuario currentUser) {
        if (preguntaId != null) {
            Pregunta pregunta = getPregunta(preguntaId);
            if (!visibilityService.canSeeActivity(currentUser, pregunta.getActividad())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La actividad de la pregunta no está asignada al curso del estudiante"
                );
            }
            return alternativaRepository.findByPreguntaIdOrderByIdAsc(preguntaId);
        }

        List<Alternativa> alternativas = alternativaRepository
                .findAllByOrderByPreguntaIdAscIdAsc();
        if (!visibilityService.isStudent(currentUser)) {
            return alternativas;
        }

        var visibleActivities = visibilityService.visibleActivityIds(currentUser);
        return alternativas.stream()
                .filter(alternativa -> alternativa.getPregunta().getActividad() != null
                        && visibleActivities.contains(
                        alternativa.getPregunta().getActividad().getId()
                ))
                .toList();
    }

    public Alternativa findById(Long id, Usuario currentUser) {
        Alternativa alternativa = getAlternativa(id);
        if (!visibilityService.canSeeActivity(
                currentUser,
                alternativa.getPregunta().getActividad()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La actividad de la alternativa no está asignada al curso del estudiante"
            );
        }
        return alternativa;
    }

    @Transactional
    public Alternativa create(
            Alternativa alternativa,
            Long preguntaId,
            Usuario currentUser
    ) {
        Pregunta pregunta = getPregunta(preguntaId);
        authoringPolicy.assertCanAuthor(currentUser, pregunta.getUnidad());
        alternativa.setPregunta(pregunta);
        if (alternativa.getEsCorrecta() == null) {
            alternativa.setEsCorrecta(false);
        }
        if (Boolean.TRUE.equals(alternativa.getEsCorrecta())
                && alternativaRepository.existsByPreguntaIdAndEsCorrectaTrue(preguntaId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La pregunta ya tiene una alternativa correcta"
            );
        }
        return alternativaRepository.save(alternativa);
    }

    @Transactional
    public Alternativa update(Long id, Alternativa cambios) {
        if (cambios.getTexto() == null && cambios.getEsCorrecta() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Alternativa alternativa = getAlternativa(id);
        if (Boolean.TRUE.equals(cambios.getEsCorrecta())
                && alternativaRepository.existsByPreguntaIdAndEsCorrectaTrueAndIdNot(
                        alternativa.getPregunta().getId(),
                        id
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La pregunta ya tiene otra alternativa correcta"
            );
        }
        if (cambios.getTexto() != null) {
            alternativa.setTexto(cambios.getTexto());
        }
        if (cambios.getEsCorrecta() != null) {
            alternativa.setEsCorrecta(cambios.getEsCorrecta());
        }
        return alternativaRepository.save(alternativa);
    }

    @Transactional
    public void delete(Long id) {
        Alternativa alternativa = getAlternativa(id);
        if (respuestaEstudianteRepository.existsByAlternativaId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una alternativa que ya fue seleccionada en respuestas de estudiantes"
            );
        }
        alternativaRepository.delete(alternativa);
    }

    private Alternativa getAlternativa(Long id) {
        return alternativaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alternativa no encontrada"));
    }

    private Pregunta getPregunta(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
    }
}
