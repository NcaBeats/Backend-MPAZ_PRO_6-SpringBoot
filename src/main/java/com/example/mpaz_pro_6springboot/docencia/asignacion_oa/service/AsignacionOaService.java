package com.example.mpaz_pro_6springboot.docencia.asignacion_oa.service;

import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOa;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOaId;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.repository.AsignacionOaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignacionOaService {

    private final AsignacionOaRepository asignacionOaRepository;
    private final AsignacionRepository asignacionRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;

    public List<AsignacionOa> findByAsignacionId(Long asignacionId) {
        getAsignacion(asignacionId);
        return asignacionOaRepository.findByAsignacionId(asignacionId);
    }

    @Transactional
    public AsignacionOa assign(Long asignacionId, Long oaId) {
        Asignacion asignacion = getAsignacion(asignacionId);
        ObjetivoAprendizaje objetivoAprendizaje = getObjetivoAprendizaje(oaId);
        Long unidadId = asignacion.getUnidad().getId();

        if (!unidadId.equals(objetivoAprendizaje.getUnidad().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El objetivo de aprendizaje debe pertenecer a la misma unidad que la asignación"
            );
        }
        if (asignacionOaRepository.existsByAsignacionIdAndObjetivoAprendizajeId(asignacionId, oaId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El objetivo de aprendizaje ya está asociado a la asignación"
            );
        }

        AsignacionOa asignacionOa = AsignacionOa.builder()
                .id(AsignacionOaId.builder()
                        .asignacionId(asignacionId)
                        .oaId(oaId)
                        .build())
                .asignacion(asignacion)
                .objetivoAprendizaje(objetivoAprendizaje)
                .unidadId(unidadId)
                .build();
        return asignacionOaRepository.save(asignacionOa);
    }

    @Transactional
    public void remove(Long asignacionId, Long oaId) {
        AsignacionOa asignacionOa = asignacionOaRepository
                .findByAsignacionIdAndObjetivoAprendizajeId(asignacionId, oaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La asociación entre la asignación y el objetivo de aprendizaje no existe"
                ));
        asignacionOaRepository.delete(asignacionOa);
    }

    private Asignacion getAsignacion(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada"));
    }

    private ObjetivoAprendizaje getObjetivoAprendizaje(Long id) {
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Objetivo de aprendizaje no encontrado"
                ));
    }
}
