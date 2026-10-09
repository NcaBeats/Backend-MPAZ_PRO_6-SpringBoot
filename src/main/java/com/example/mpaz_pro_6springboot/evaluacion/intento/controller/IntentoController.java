package com.example.mpaz_pro_6springboot.evaluacion.intento.controller;

import com.example.mpaz_pro_6springboot.evaluacion.intento.dto.response.IntentoResponse;
import com.example.mpaz_pro_6springboot.evaluacion.intento.mapper.IntentoMapper;
import com.example.mpaz_pro_6springboot.evaluacion.intento.service.IntentoService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class IntentoController {

    private final IntentoService intentoService;
    private final IntentoMapper intentoMapper;

    @GetMapping("/intentos")
    public ResponseEntity<List<IntentoResponse>> findAll(
            @RequestParam(required = false) @Positive Long estudianteId,
            @RequestParam(required = false) @Positive Long actividadId
    ) {
        return ResponseEntity.ok(intentoService.findAll(estudianteId, actividadId).stream()
                .map(intentoMapper::toResponse)
                .toList());
    }

    @GetMapping("/intentos/{id}")
    public ResponseEntity<IntentoResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(intentoMapper.toResponse(intentoService.findById(id)));
    }

    @PostMapping("/estudiantes/{estudianteId}/actividades/{actividadId}/intentos")
    public ResponseEntity<IntentoResponse> create(
            @PathVariable @Positive Long estudianteId,
            @PathVariable @Positive Long actividadId
    ) {
        IntentoResponse created = intentoMapper.toResponse(intentoService.create(estudianteId, actividadId));
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/intentos/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/intentos/{id}/completar")
    public ResponseEntity<IntentoResponse> completar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(intentoMapper.toResponse(intentoService.completar(id)));
    }
}
