package com.example.mpaz_pro_6springboot.curriculo.actividad.service;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActividadService {

    private static final int ORDEN_PREDETERMINADO = 1;

    private final ActividadRepository actividadRepository;
    private final UnidadRepository unidadRepository;
    private final PreguntaRepository preguntaRepository;
    private final AsignacionActividadRepository asignacionActividadRepository;
    private final IntentoRepository intentoRepository;

    public List<Actividad> findAll(Long unidadId) {
        if (unidadId == null) {
            return actividadRepository.findAllByOrderByUnidadIdAscOrdenAscIdAsc();
        }

        getUnidad(unidadId);
        return actividadRepository.findByUnidadIdOrderByOrdenAsc(unidadId);
    }

    public Actividad findById(Long id) {
        return getActividad(id);
    }

    @Transactional
    public Actividad create(Actividad actividad, Long unidadId) {
        Unidad unidad = getUnidad(unidadId);
        if (actividad.getOrden() == null) {
            actividad.setOrden(ORDEN_PREDETERMINADO);
        }
        validateTituloUnico(unidadId, actividad.getTitulo(), null);
        validatePruebaFinalUnica(unidadId, actividad.getTipo(), null);
        actividad.setUnidad(unidad);
        return actividadRepository.save(actividad);
    }

    @Transactional
    public Actividad update(Long id, Actividad cambios) {
        if (isEmptyUpdate(cambios)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Actividad actividad = getActividad(id);
        Long unidadId = actividad.getUnidad().getId();
        if (cambios.getTitulo() != null) {
            validateTituloUnico(unidadId, cambios.getTitulo(), id);
            actividad.setTitulo(cambios.getTitulo());
        }
        if (cambios.getTipo() != null) {
            validatePruebaFinalUnica(unidadId, cambios.getTipo(), id);
            actividad.setTipo(cambios.getTipo());
        }
        if (cambios.getOrden() != null) {
            actividad.setOrden(cambios.getOrden());
        }
        return actividadRepository.save(actividad);
    }

    @Transactional
    public void delete(Long id) {
        Actividad actividad = getActividad(id);
        if (preguntaRepository.existsByActividadId(id)
                || asignacionActividadRepository.existsByActividadId(id)
                || intentoRepository.existsByActividadId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una actividad que tiene preguntas, asignaciones o intentos asociados"
            );
        }
        actividadRepository.delete(actividad);
    }

    private boolean isEmptyUpdate(Actividad cambios) {
        return cambios.getTitulo() == null
                && cambios.getTipo() == null
                && cambios.getOrden() == null;
    }

    private void validateTituloUnico(Long unidadId, String titulo, Long excludedId) {
        boolean exists = excludedId == null
                ? actividadRepository.existsByUnidadIdAndTitulo(unidadId, titulo)
                : actividadRepository.existsByUnidadIdAndTituloAndIdNot(unidadId, titulo, excludedId);
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una actividad con ese título en la unidad"
            );
        }
    }

    private void validatePruebaFinalUnica(Long unidadId, TipoActividad tipo, Long excludedId) {
        if (tipo != TipoActividad.PRUEBA_FINAL) {
            return;
        }
        boolean exists = excludedId == null
                ? actividadRepository.existsByUnidadIdAndTipo(unidadId, TipoActividad.PRUEBA_FINAL)
                : actividadRepository.existsByUnidadIdAndTipoAndIdNot(unidadId, TipoActividad.PRUEBA_FINAL, excludedId);
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La unidad ya tiene una prueba final"
            );
        }
    }

    private Actividad getActividad(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }

    private Unidad getUnidad(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));
    }
}
