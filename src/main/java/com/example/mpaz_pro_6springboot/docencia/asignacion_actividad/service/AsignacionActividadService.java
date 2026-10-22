package com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.service;

import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividad;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividadId;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignacionActividadService {

    private final AsignacionActividadRepository asignacionActividadRepository;
    private final AsignacionRepository asignacionRepository;
    private final ActividadRepository actividadRepository;

    public List<AsignacionActividad> findByAsignacionId(Long asignacionId) {
        getAsignacion(asignacionId);
        return asignacionActividadRepository.findByAsignacionId(asignacionId);
    }

    @Transactional
    public AsignacionActividad assign(Long asignacionId, Long actividadId) {
        Asignacion asignacion = getAsignacion(asignacionId);
        Actividad actividad = getActividad(actividadId);
        Long unidadId = asignacion.getUnidad().getId();

        if (!unidadId.equals(actividad.getUnidad().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La actividad debe pertenecer a la misma unidad que la asignación"
            );
        }
        if (actividad.getTipo() == TipoActividad.PRUEBA_FINAL) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La prueba final no se agrega como actividad de una asignación"
            );
        }
        if (asignacionActividadRepository.existsByAsignacionIdAndActividadId(asignacionId, actividadId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La actividad ya está asociada a la asignación"
            );
        }

        AsignacionActividad asignacionActividad = AsignacionActividad.builder()
                .id(AsignacionActividadId.builder()
                        .asignacionId(asignacionId)
                        .actividadId(actividadId)
                        .build())
                .asignacion(asignacion)
                .actividad(actividad)
                .unidadId(unidadId)
                .build();
        return asignacionActividadRepository.save(asignacionActividad);
    }

    @Transactional
    public void remove(Long asignacionId, Long actividadId) {
        AsignacionActividad asignacionActividad = asignacionActividadRepository
                .findByAsignacionIdAndActividadId(asignacionId, actividadId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La asociación entre la asignación y la actividad no existe"
                ));
        asignacionActividadRepository.delete(asignacionActividad);
    }

    private Asignacion getAsignacion(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada"));
    }

    private Actividad getActividad(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }
}
