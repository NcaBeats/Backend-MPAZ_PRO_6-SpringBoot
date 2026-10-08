package com.example.mpaz_pro_6springboot.curriculo.contenido.controller;

import com.example.mpaz_pro_6springboot.curriculo.contenido.dto.request.ContenidoCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.contenido.dto.request.ContenidoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.contenido.dto.response.ContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.contenido.mapper.ContenidoMapper;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.service.ContenidoService;
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
@RequestMapping("/api/contenidos")
@RequiredArgsConstructor
@Validated
public class ContenidoController {

    private final ContenidoService contenidoService;
    private final ContenidoMapper contenidoMapper;

    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> findAll(
            @RequestParam(required = false) @Positive Long oaId
    ) {
        return ResponseEntity.ok(contenidoService.findAll(oaId).stream()
                .map(contenidoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenidoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contenidoMapper.toResponse(contenidoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ContenidoResponse> create(@Valid @RequestBody ContenidoCreateRequest request) {
        Contenido contenido = contenidoMapper.toEntity(request);
        ContenidoResponse created = contenidoMapper.toResponse(
                contenidoService.create(contenido, request.oaId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContenidoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ContenidoUpdateRequest request
    ) {
        Contenido cambios = contenidoMapper.toEntity(request);
        return ResponseEntity.ok(contenidoMapper.toResponse(contenidoService.update(id, cambios)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contenidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
