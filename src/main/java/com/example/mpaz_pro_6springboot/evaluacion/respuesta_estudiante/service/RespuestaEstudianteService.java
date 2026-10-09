package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.service;

import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.model.RespuestaEstudiante;
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
public class RespuestaEstudianteService {

    private final RespuestaEstudianteRepository respuestaEstudianteRepository;
    private final IntentoRepository intentoRepository;
    private final PreguntaRepository preguntaRepository;
    private final AlternativaRepository alternativaRepository;

    public List<RespuestaEstudiante> findByIntentoId(Long intentoId) {
        getIntento(intentoId);
        return respuestaEstudianteRepository.findByIntentoIdOrderByIdAsc(intentoId);
    }

    @Transactional
    public RespuestaEstudiante create(RespuestaEstudiante respuesta, Long intentoId, Long preguntaId) {
        Intento intento = getIntento(intentoId);
        Pregunta pregunta = getPregunta(preguntaId);

        if (intento.getFechaFin() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El intento ya está completado y no admite nuevas respuestas"
            );
        }
        if (respuesta.getAlternativaId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar la alternativa seleccionada"
            );
        }

        Alternativa alternativa = getAlternativa(respuesta.getAlternativaId());
        if (alternativa.getPregunta() == null
                || !alternativa.getPregunta().getId().equals(preguntaId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La alternativa seleccionada no pertenece a la pregunta indicada"
            );
        }
        if (pregunta.getActividad() == null
                || !pregunta.getActividad().getId().equals(intento.getActividad().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La pregunta no pertenece a la actividad del intento"
            );
        }
        if (respuestaEstudianteRepository.existsByIntentoIdAndPreguntaId(intentoId, preguntaId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El intento ya tiene una respuesta registrada para esta pregunta"
            );
        }

        respuesta.setIntento(intento);
        respuesta.setPregunta(pregunta);
        respuesta.setEsCorrecta(alternativa.getEsCorrecta());
        return respuestaEstudianteRepository.save(respuesta);
    }

    private Intento getIntento(Long id) {
        return intentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intento no encontrado"));
    }

    private Pregunta getPregunta(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
    }

    private Alternativa getAlternativa(Long id) {
        return alternativaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alternativa no encontrada"));
    }
}
