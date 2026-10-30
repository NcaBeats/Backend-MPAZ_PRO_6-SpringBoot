package com.example.mpaz_pro_6springboot.evaluacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.repository.AlternativaPreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.PreguntaActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.model.Respuesta;
import com.example.mpaz_pro_6springboot.evaluacion.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.evaluacion.repository.RespuestaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Respuesta}.
 *
 * <p>Valida que la pregunta pertenezca a la actividad del intento, que la
 * alternativa pertenezca a la pregunta y que la respuesta no este duplicada.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final IntentoRepository intentoRepository;
    private final PreguntaRepository preguntaRepository;
    private final AlternativaRepository alternativaRepository;
    private final PreguntaActividadRepository preguntaActividadRepository;
    private final AlternativaPreguntaRepository alternativaPreguntaRepository;

    public List<Respuesta> listar() {
        return respuestaRepository.findAll();
    }

    public List<Respuesta> listarPorIntento(Long intentoId) {
        return respuestaRepository.findAllByIntentoId(intentoId);
    }

    public List<Respuesta> listarPorPregunta(Long preguntaId) {
        return respuestaRepository.findAllByPreguntaId(preguntaId);
    }

    public List<Respuesta> listarPorIntentoYPregunta(Long intentoId, Long preguntaId) {
        return respuestaRepository.findAllByIntentoIdAndPreguntaId(intentoId, preguntaId);
    }

    public Respuesta obtener(Long id) {
        return respuestaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Respuesta", id));
    }

    @Transactional
    public Respuesta crear(Respuesta datos) {
        Validaciones.requerido(datos, "respuesta");

        Intento intento = resolverIntento(datos.getIntento());
        Pregunta pregunta = resolverPregunta(datos.getPregunta());
        Alternativa alternativa = resolverAlternativa(datos.getAlternativa());

        if (intento.getFechaFin() != null) {
            throw new ConflictoException("No se pueden registrar respuestas en un intento cerrado");
        }

        Long actividadId = intento.getActividad().getId();
        if (!preguntaActividadRepository.existsByActividadIdAndPreguntaId(
                actividadId, pregunta.getId())) {
            throw new ReglaNegocioException(
                    "La pregunta no pertenece a la actividad del intento");
        }
        if (!alternativaPreguntaRepository.existsByPreguntaIdAndAlternativaId(
                pregunta.getId(), alternativa.getId())) {
            throw new ReglaNegocioException(
                    "La alternativa no pertenece a la pregunta indicada");
        }
        if (respuestaRepository.existsByIntentoIdAndPreguntaIdAndAlternativaId(
                intento.getId(), pregunta.getId(), alternativa.getId())) {
            throw new ConflictoException("La respuesta ya fue registrada en este intento");
        }

        datos.setId(null);
        datos.setIntento(intento);
        datos.setPregunta(pregunta);
        datos.setAlternativa(alternativa);
        return respuestaRepository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        respuestaRepository.delete(obtener(id));
    }

    private Intento resolverIntento(Intento intento) {
        if (intento == null || intento.getId() == null) {
            throw new ReglaNegocioException("El campo intento es obligatorio");
        }
        return intentoRepository.findById(intento.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Intento", intento.getId()));
    }

    private Pregunta resolverPregunta(Pregunta pregunta) {
        if (pregunta == null || pregunta.getId() == null) {
            throw new ReglaNegocioException("El campo pregunta es obligatorio");
        }
        return preguntaRepository.findById(pregunta.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pregunta", pregunta.getId()));
    }

    private Alternativa resolverAlternativa(Alternativa alternativa) {
        if (alternativa == null || alternativa.getId() == null) {
            throw new ReglaNegocioException("El campo alternativa es obligatorio");
        }
        return alternativaRepository.findById(alternativa.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Alternativa", alternativa.getId()));
    }
}
