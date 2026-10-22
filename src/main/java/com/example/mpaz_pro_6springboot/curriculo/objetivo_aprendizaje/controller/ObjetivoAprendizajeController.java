package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.controller;

import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.request.ObjetivoAprendizajeCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.request.ObjetivoAprendizajeUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.dto.response.ObjetivoAprendizajeResponse;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.mapper.ObjetivoAprendizajeMapper;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.service.ObjetivoAprendizajeService;
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
@RequestMapping("/api/objetivos-aprendizaje")
@RequiredArgsConstructor
@Validated
public class ObjetivoAprendizajeController {

    private final ObjetivoAprendizajeService objetivoAprendizajeService;
    private final ObjetivoAprendizajeMapper objetivoAprendizajeMapper;

    @GetMapping
    public ResponseEntity<List<ObjetivoAprendizajeResponse>> findAll(
            @RequestParam(required = false) @Positive Long unidadId
    ) {
        return ResponseEntity.ok(objetivoAprendizajeService.findAll(unidadId).stream()
                .map(objetivoAprendizajeMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoAprendizajeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(objetivoAprendizajeMapper.toResponse(
                objetivoAprendizajeService.findById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<ObjetivoAprendizajeResponse> create(
            @Valid @RequestBody ObjetivoAprendizajeCreateRequest request
    ) {
        ObjetivoAprendizaje objetivoAprendizaje = objetivoAprendizajeMapper.toEntity(request);
        ObjetivoAprendizajeResponse created = objetivoAprendizajeMapper.toResponse(
                objetivoAprendizajeService.create(objetivoAprendizaje, request.unidadId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoAprendizajeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ObjetivoAprendizajeUpdateRequest request
    ) {
        ObjetivoAprendizaje cambios = objetivoAprendizajeMapper.toEntity(request);
        ObjetivoAprendizaje actualizada = objetivoAprendizajeService.update(id, cambios);
        return ResponseEntity.ok(objetivoAprendizajeMapper.toResponse(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        objetivoAprendizajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
