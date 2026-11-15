package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeContenido;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeUnidadRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de la relacion N:M {@link ObjetivoAprendizajeContenido}.
 *
 * <p>La unidad se toma del contenido. El OA debe estar ofrecido en esa misma
 * unidad, condicion que la FK compuesta de PostgreSQL tambien exige.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObjetivoAprendizajeContenidoService {

    private final ObjetivoAprendizajeContenidoRepository objetivoAprendizajeContenidoRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final ContenidoRepository contenidoRepository;
    private final ObjetivoAprendizajeUnidadRepository objetivoAprendizajeUnidadRepository;

    public List<ObjetivoAprendizajeContenido> listarPorContenido(Long contenidoId) {
        return objetivoAprendizajeContenidoRepository.findAllByContenidoId(contenidoId);
    }

    public List<ObjetivoAprendizajeContenido> listarPorObjetivo(Long objetivoAprendizajeId) {
        return objetivoAprendizajeContenidoRepository.findAllByObjetivoAprendizajeId(
                objetivoAprendizajeId);
    }

    public List<ObjetivoAprendizajeContenido> listarPorUnidad(Long unidadId) {
        return objetivoAprendizajeContenidoRepository.findAllByUnidadId(unidadId);
    }

    public ObjetivoAprendizajeContenido obtener(Long id) {
        return objetivoAprendizajeContenidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "ObjetivoAprendizajeContenido", id));
    }

    @Transactional
    public ObjetivoAprendizajeContenido crear(Long objetivoAprendizajeId, Long contenidoId) {
        ObjetivoAprendizaje objetivo = resolverObjetivo(objetivoAprendizajeId);
        Contenido contenido = resolverContenido(contenidoId);
        Unidad unidad = contenido.getUnidad();

        if (!objetivoAprendizajeUnidadRepository.existsByObjetivoAprendizajeIdAndUnidadId(
                objetivo.getId(), unidad.getId())) {
            throw new ReglaNegocioException(
                    "El objetivo de aprendizaje no esta ofrecido en la unidad del contenido");
        }
        if (objetivoAprendizajeContenidoRepository.existsByContenidoIdAndObjetivoAprendizajeId(
                contenido.getId(), objetivo.getId())) {
            throw new ConflictoException(
                    "El objetivo de aprendizaje ya esta vinculado a este contenido");
        }

        return objetivoAprendizajeContenidoRepository.save(
                ObjetivoAprendizajeContenido.builder()
                        .objetivoAprendizaje(objetivo)
                        .contenido(contenido)
                        .unidad(unidad)
                        .build());
    }

    @Transactional
    public void eliminar(Long id) {
        objetivoAprendizajeContenidoRepository.delete(obtener(id));
    }

    private ObjetivoAprendizaje resolverObjetivo(Long id) {
        if (id == null) {
            throw new ReglaNegocioException("El campo objetivoAprendizaje es obligatorio");
        }
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("ObjetivoAprendizaje", id));
    }

    private Contenido resolverContenido(Long id) {
        if (id == null) {
            throw new ReglaNegocioException("El campo contenido es obligatorio");
        }
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contenido", id));
    }
}
