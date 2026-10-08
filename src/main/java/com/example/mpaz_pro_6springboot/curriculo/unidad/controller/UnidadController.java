package com.example.mpaz_pro_6springboot.curriculo.unidad.controller;

import com.example.mpaz_pro_6springboot.curriculo.unidad.dto.request.UnidadCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.unidad.dto.request.UnidadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.unidad.dto.response.UnidadResponse;
import com.example.mpaz_pro_6springboot.curriculo.unidad.mapper.UnidadMapper;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.service.UnidadService;
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
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
@Validated
public class UnidadController {

    private final UnidadService unidadService;
    private final UnidadMapper unidadMapper;

    @GetMapping
    public ResponseEntity<List<UnidadResponse>> findAll(
            @RequestParam(required = false) @Positive Long asignaturaId
    ) {
        return ResponseEntity.ok(unidadService.findAll(asignaturaId).stream()
                .map(unidadMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMapper.toResponse(unidadService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UnidadResponse> create(@Valid @RequestBody UnidadCreateRequest request) {
        Unidad unidad = unidadMapper.toEntity(request);
        UnidadResponse created = unidadMapper.toResponse(unidadService.create(unidad, request.asignaturaId()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UnidadUpdateRequest request
    ) {
        Unidad cambios = unidadMapper.toEntity(request);
        Unidad actualizada = unidadService.update(id, cambios, request.autorizadaPorId());
        return ResponseEntity.ok(unidadMapper.toResponse(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        unidadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
