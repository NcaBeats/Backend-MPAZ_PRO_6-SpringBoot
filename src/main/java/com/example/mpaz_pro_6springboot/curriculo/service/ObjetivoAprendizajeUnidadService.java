package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeUnidad;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeUnidadRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.UnidadRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de la relacion N:M {@link ObjetivoAprendizajeUnidad}.
 *
 * <p>Un OA puede ofrecerse en varias unidades (curriculum en espiral). La
 * pareja OA-unidad es unica.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObjetivoAprendizajeUnidadService {

    private final ObjetivoAprendizajeUnidadRepository objetivoAprendizajeUnidadRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final UnidadRepository unidadRepository;

    public List<ObjetivoAprendizajeUnidad> listarPorUnidad(Long unidadId) {
        return objetivoAprendizajeUnidadRepository.findAllByUnidadId(unidadId);
    }

    public List<ObjetivoAprendizajeUnidad> listarPorObjetivo(Long objetivoAprendizajeId) {
        return objetivoAprendizajeUnidadRepository.findAllByObjetivoAprendizajeId(
                objetivoAprendizajeId);
    }

    public ObjetivoAprendizajeUnidad obtener(Long id) {
        return objetivoAprendizajeUnidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "ObjetivoAprendizajeUnidad", id));
    }

    public boolean existe(Long objetivoAprendizajeId, Long unidadId) {
        return objetivoAprendizajeUnidadRepository
                .existsByObjetivoAprendizajeIdAndUnidadId(objetivoAprendizajeId, unidadId);
    }

    public long contarPorUnidad(Long unidadId) {
        return objetivoAprendizajeUnidadRepository.countByUnidadId(unidadId);
    }

    @Transactional
    public ObjetivoAprendizajeUnidad crear(Long objetivoAprendizajeId, Long unidadId) {
        ObjetivoAprendizaje objetivo = resolverObjetivo(objetivoAprendizajeId);
        Unidad unidad = resolverUnidad(unidadId);

        if (objetivoAprendizajeUnidadRepository.existsByObjetivoAprendizajeIdAndUnidadId(
                objetivo.getId(), unidad.getId())) {
            throw new ConflictoException(
                    "El objetivo de aprendizaje ya esta ofrecido en la unidad indicada");
        }

        return objetivoAprendizajeUnidadRepository.save(
                ObjetivoAprendizajeUnidad.builder()
                        .objetivoAprendizaje(objetivo)
                        .unidad(unidad)
                        .build());
    }

    @Transactional
    public void eliminar(Long id) {
        objetivoAprendizajeUnidadRepository.delete(obtener(id));
    }

    private ObjetivoAprendizaje resolverObjetivo(Long id) {
        if (id == null) {
            throw new ReglaNegocioException(
                    "El campo objetivoAprendizaje es obligatorio");
        }
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("ObjetivoAprendizaje", id));
    }

    private Unidad resolverUnidad(Long id) {
        if (id == null) {
            throw new ReglaNegocioException(
                    "El campo unidad es obligatorio");
        }
        return unidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad", id));
    }
}
