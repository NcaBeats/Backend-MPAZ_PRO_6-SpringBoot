package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.model.AlternativaPregunta;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.repository.AlternativaPreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.PreguntaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de la relacion N:M {@link AlternativaPregunta}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlternativaPreguntaService {

    private final AlternativaPreguntaRepository alternativaPreguntaRepository;
    private final PreguntaRepository preguntaRepository;
    private final AlternativaRepository alternativaRepository;

    public List<AlternativaPregunta> listar() {
        return alternativaPreguntaRepository.findAll();
    }

    public List<AlternativaPregunta> listarPorPregunta(Long preguntaId) {
        return alternativaPreguntaRepository.findAllByPreguntaId(preguntaId);
    }

    public List<AlternativaPregunta> listarPorAlternativa(Long alternativaId) {
        return alternativaPreguntaRepository.findAllByAlternativaId(alternativaId);
    }

    public AlternativaPregunta obtener(Long id) {
        return alternativaPreguntaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "AlternativaPregunta", id));
    }

    public boolean existe(Long preguntaId, Long alternativaId) {
        return alternativaPreguntaRepository
                .existsByPreguntaIdAndAlternativaId(preguntaId, alternativaId);
    }

    public long contarCorrectas(Long preguntaId) {
        return alternativaPreguntaRepository
                .countByPreguntaIdAndAlternativaCorrectaTrue(preguntaId);
    }

    @Transactional
    public AlternativaPregunta crear(Long preguntaId, Long alternativaId) {
        Pregunta pregunta = resolverPregunta(preguntaId);
        Alternativa alternativa = resolverAlternativa(alternativaId);

        if (alternativaPreguntaRepository.existsByPreguntaIdAndAlternativaId(
                pregunta.getId(), alternativa.getId())) {
            throw new ConflictoException(
                    "La alternativa ya esta asociada a la pregunta indicada");
        }

        return alternativaPreguntaRepository.save(
                AlternativaPregunta.builder()
                        .pregunta(pregunta)
                        .alternativa(alternativa)
                        .build());
    }

    @Transactional
    public void eliminar(Long id) {
        alternativaPreguntaRepository.delete(obtener(id));
    }

    private Pregunta resolverPregunta(Long id) {
        if (id == null) {
            throw new ReglaNegocioException("El campo pregunta es obligatorio");
        }
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pregunta", id));
    }

    private Alternativa resolverAlternativa(Long id) {
        if (id == null) {
            throw new ReglaNegocioException("El campo alternativa es obligatorio");
        }
        return alternativaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alternativa", id));
    }
}
