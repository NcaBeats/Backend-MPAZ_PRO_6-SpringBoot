package com.example.mpaz_pro_6springboot.docencia.asignacion_oa.controller;

import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.dto.response.AsignacionOaResponse;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.mapper.AsignacionOaMapper;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOa;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.service.AsignacionOaService;
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
@RequestMapping("/api/asignaciones/{asignacionId}/objetivos-aprendizaje")
@RequiredArgsConstructor
@Validated
public class AsignacionOaController {

    private final AsignacionOaService asignacionOaService;
    private final AsignacionOaMapper asignacionOaMapper;

    @GetMapping
    public ResponseEntity<List<AsignacionOaResponse>> findByAsignacionId(
            @PathVariable @Positive Long asignacionId
    ) {
        return ResponseEntity.ok(asignacionOaService.findByAsignacionId(asignacionId).stream()
                .map(asignacionOaMapper::toResponse)
                .toList());
    }

    @PostMapping("/{oaId}")
    public ResponseEntity<AsignacionOaResponse> assign(
            @PathVariable @Positive Long asignacionId,
            @PathVariable @Positive Long oaId
    ) {
        AsignacionOaResponse response = asignacionOaMapper.toResponse(
                asignacionOaService.assign(asignacionId, oaId)
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{oaId}")
    public ResponseEntity<Void> remove(
            @PathVariable @Positive Long asignacionId,
            @PathVariable @Positive Long oaId
    ) {
        asignacionOaService.remove(asignacionId, oaId);
        return ResponseEntity.noContent().build();
    }
}
