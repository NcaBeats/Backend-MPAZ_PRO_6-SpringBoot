package com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.controller;

import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.dto.response.AsignacionActividadResponse;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.mapper.AsignacionActividadMapper;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividad;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.service.AsignacionActividadService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones/{asignacionId}/actividades")
@RequiredArgsConstructor
@Validated
public class AsignacionActividadController {

    private final AsignacionActividadService asignacionActividadService;
    private final AsignacionActividadMapper asignacionActividadMapper;

    @GetMapping
    public ResponseEntity<List<AsignacionActividadResponse>> findByAsignacionId(
            @PathVariable @Positive Long asignacionId
    ) {
        return ResponseEntity.ok(asignacionActividadService.findByAsignacionId(asignacionId).stream()
                .map(asignacionActividadMapper::toResponse)
                .toList());
    }

    @PostMapping("/{actividadId}")
    public ResponseEntity<AsignacionActividadResponse> assign(
            @PathVariable @Positive Long asignacionId,
            @PathVariable @Positive Long actividadId
    ) {
        AsignacionActividadResponse response = asignacionActividadMapper.toResponse(
                asignacionActividadService.assign(asignacionId, actividadId)
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{actividadId}")
    public ResponseEntity<Void> remove(
            @PathVariable @Positive Long asignacionId,
            @PathVariable @Positive Long actividadId
    ) {
        asignacionActividadService.remove(asignacionId, actividadId);
        return ResponseEntity.noContent().build();
    }
}
