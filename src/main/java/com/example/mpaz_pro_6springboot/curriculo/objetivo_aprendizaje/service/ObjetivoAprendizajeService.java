package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.service;

import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.repository.AsignacionOaRepository;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.repository.DetalleInformeOaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObjetivoAprendizajeService {

    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final UnidadRepository unidadRepository;
    private final ContenidoRepository contenidoRepository;
    private final PreguntaRepository preguntaRepository;
    private final AsignacionOaRepository asignacionOaRepository;
    private final DetalleInformeOaRepository detalleInformeOaRepository;

    public List<ObjetivoAprendizaje> findAll(Long unidadId) {
        if (unidadId == null) {
            return objetivoAprendizajeRepository.findAllByOrderByUnidadIdAscCodigoAsc();
        }

        getUnidad(unidadId);
        return objetivoAprendizajeRepository.findByUnidadIdOrderByCodigoAsc(unidadId);
    }

    public ObjetivoAprendizaje findById(Long id) {
        return getObjetivoAprendizaje(id);
    }

    @Transactional
    public ObjetivoAprendizaje create(ObjetivoAprendizaje objetivoAprendizaje, Long unidadId) {
        Unidad unidad = getUnidad(unidadId);
        objetivoAprendizaje.setUnidad(unidad);
        validateCodigoDisponible(unidadId, objetivoAprendizaje.getCodigo());
        return objetivoAprendizajeRepository.save(objetivoAprendizaje);
    }

    @Transactional
    public ObjetivoAprendizaje update(Long id, ObjetivoAprendizaje cambios) {
        if (isEmptyUpdate(cambios)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        ObjetivoAprendizaje objetivoAprendizaje = getObjetivoAprendizaje(id);
        if (cambios.getCodigo() != null) {
            validateCodigoDisponible(objetivoAprendizaje.getUnidad().getId(), cambios.getCodigo(), id);
            objetivoAprendizaje.setCodigo(cambios.getCodigo());
        }
        if (cambios.getDescripcion() != null) {
            objetivoAprendizaje.setDescripcion(cambios.getDescripcion());
        }
        if (cambios.getEje() != null) {
            objetivoAprendizaje.setEje(cambios.getEje());
        }
        if (cambios.getTextoReferencia() != null) {
            objetivoAprendizaje.setTextoReferencia(cambios.getTextoReferencia());
        }

        return objetivoAprendizajeRepository.save(objetivoAprendizaje);
    }

    @Transactional
    public void delete(Long id) {
        ObjetivoAprendizaje objetivoAprendizaje = getObjetivoAprendizaje(id);
        if (contenidoRepository.existsByOaId(id)
                || preguntaRepository.existsByOaId(id)
                || asignacionOaRepository.existsByObjetivoAprendizajeId(id)
                || detalleInformeOaRepository.existsByOaId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un objetivo de aprendizaje con contenidos, preguntas, asignaciones o informes asociados"
            );
        }

        objetivoAprendizajeRepository.delete(objetivoAprendizaje);
    }

    private boolean isEmptyUpdate(ObjetivoAprendizaje cambios) {
        return cambios.getCodigo() == null
                && cambios.getDescripcion() == null
                && cambios.getEje() == null
                && cambios.getTextoReferencia() == null;
    }

    private void validateCodigoDisponible(Long unidadId, String codigo) {
        if (codigo != null && objetivoAprendizajeRepository.existsByUnidadIdAndCodigo(unidadId, codigo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un objetivo de aprendizaje con ese código en la unidad"
            );
        }
    }

    private void validateCodigoDisponible(Long unidadId, String codigo, Long id) {
        if (codigo != null && objetivoAprendizajeRepository.existsByUnidadIdAndCodigoAndIdNot(unidadId, codigo, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un objetivo de aprendizaje con ese código en la unidad"
            );
        }
    }

    private Unidad getUnidad(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));
    }

    private ObjetivoAprendizaje getObjetivoAprendizaje(Long id) {
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Objetivo de aprendizaje no encontrado"
                ));
    }
}
