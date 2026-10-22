package com.example.mpaz_pro_6springboot.curriculo.pregunta.controller;

import com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.request.PreguntaCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.request.PreguntaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.dto.response.PreguntaResponse;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.mapper.PreguntaMapper;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.service.PreguntaService;
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
@RequestMapping("/api/preguntas")
@RequiredArgsConstructor
@Validated
public class PreguntaController {

    private final PreguntaService preguntaService;
    private final PreguntaMapper preguntaMapper;

    @GetMapping
    public ResponseEntity<List<PreguntaResponse>> findAll(
            @RequestParam(required = false) @Positive Long actividadId,
            @RequestParam(required = false) @Positive Long oaId
    ) {
        return ResponseEntity.ok(preguntaService.findAll(actividadId, oaId).stream()
                .map(preguntaMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreguntaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(preguntaMapper.toResponse(preguntaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<PreguntaResponse> create(@Valid @RequestBody PreguntaCreateRequest request) {
        Pregunta pregunta = preguntaMapper.toEntity(request);
        PreguntaResponse created = preguntaMapper.toResponse(
                preguntaService.create(pregunta, request.actividadId(), request.unidadId(), request.oaId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreguntaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PreguntaUpdateRequest request
    ) {
        Pregunta cambios = preguntaMapper.toEntity(request);
        return ResponseEntity.ok(preguntaMapper.toResponse(preguntaService.update(id, cambios)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        preguntaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
