package com.example.mpaz_pro_6springboot.docencia.asignacion.controller;

import com.example.mpaz_pro_6springboot.docencia.asignacion.dto.request.AsignacionCreateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion.dto.request.AsignacionUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.asignacion.dto.response.AsignacionResponse;
import com.example.mpaz_pro_6springboot.docencia.asignacion.mapper.AsignacionMapper;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.service.AsignacionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
@Validated
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final AsignacionMapper asignacionMapper;

    @GetMapping
    public ResponseEntity<List<AsignacionResponse>> findAll(
            @RequestParam(required = false) @Positive Long cursoId,
            @RequestParam(required = false) @Positive Long unidadId,
            @RequestParam(required = false) @Positive Long docenteId
    ) {
        return ResponseEntity.ok(asignacionService.findAll(cursoId, unidadId, docenteId).stream()
                .map(asignacionMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(asignacionMapper.toResponse(asignacionService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<AsignacionResponse> create(@Valid @RequestBody AsignacionCreateRequest request) {
        Asignacion asignacion = asignacionMapper.toEntity(request);
        AsignacionResponse created = asignacionMapper.toResponse(
                asignacionService.create(asignacion, request.cursoId(), request.unidadId(), request.docenteId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsignacionResponse> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody AsignacionUpdateRequest request
    ) {
        Asignacion cambios = asignacionMapper.toEntity(request);
        return ResponseEntity.ok(asignacionMapper.toResponse(asignacionService.update(id, cambios)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        asignacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
