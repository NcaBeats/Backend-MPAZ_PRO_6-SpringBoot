package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.model.PreguntaActividad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.PreguntaActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.PreguntaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de la relacion N:M {@link PreguntaActividad}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreguntaActividadService {

    private final PreguntaActividadRepository preguntaActividadRepository;
    private final ActividadRepository actividadRepository;
    private final PreguntaRepository preguntaRepository;

    public List<PreguntaActividad> listar() {
        return preguntaActividadRepository.findAll();
    }

    public List<PreguntaActividad> listarPorActividad(Long actividadId) {
        return preguntaActividadRepository.findAllByActividadId(actividadId);
    }

    public List<PreguntaActividad> listarPorPregunta(Long preguntaId) {
        return preguntaActividadRepository.findAllByPreguntaId(preguntaId);
    }

    public PreguntaActividad obtener(Long id) {
        return preguntaActividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("PreguntaActividad", id));
    }

    public boolean existe(Long actividadId, Long preguntaId) {
        return preguntaActividadRepository.existsByActividadIdAndPreguntaId(
                actividadId, preguntaId);
    }

    @Transactional
    public PreguntaActividad crear(Long actividadId, Long preguntaId) {
        Actividad actividad = resolverActividad(actividadId);
        Pregunta pregunta = resolverPregunta(preguntaId);

        if (preguntaActividadRepository.existsByActividadIdAndPreguntaId(
                actividad.getId(), pregunta.getId())) {
            throw new ConflictoException(
                    "La pregunta ya esta asociada a la actividad indicada");
        }

        return preguntaActividadRepository.save(
                PreguntaActividad.builder()
                        .actividad(actividad)
                        .pregunta(pregunta)
                        .build());
    }

    @Transactional
    public void eliminar(Long id) {
        preguntaActividadRepository.delete(obtener(id));
    }

    private Actividad resolverActividad(Long id) {
        if (id == null) {
            throw new ReglaNegocioException("El campo actividad es obligatorio");
        }
        return actividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", id));
    }

    private Pregunta resolverPregunta(Long id) {
        if (id == null) {
            throw new ReglaNegocioException("El campo pregunta es obligatorio");
        }
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pregunta", id));
    }
}
