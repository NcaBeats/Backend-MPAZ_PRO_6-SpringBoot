package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.controller;

import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.request.RecursoContenidoCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.request.RecursoContenidoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.dto.response.RecursoContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.mapper.RecursoContenidoMapper;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.service.RecursoContenidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/recursos-contenido")
@RequiredArgsConstructor
public class RecursoContenidoController {

    private final RecursoContenidoService recursoContenidoService;
    private final RecursoContenidoMapper recursoContenidoMapper;

    @GetMapping
    public ResponseEntity<List<RecursoContenidoResponse>> findAll(
            @RequestParam(required = false) Long contenidoId
    ) {
        return ResponseEntity.ok(recursoContenidoService.findAll(contenidoId).stream()
                .map(recursoContenidoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoContenidoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(recursoContenidoMapper.toResponse(recursoContenidoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<RecursoContenidoResponse> create(
            @Valid @RequestBody RecursoContenidoCreateRequest request
    ) {
        RecursoContenido recurso = recursoContenidoMapper.toEntity(request);
        RecursoContenidoResponse created = recursoContenidoMapper.toResponse(
                recursoContenidoService.create(recurso, request.contenidoId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoContenidoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RecursoContenidoUpdateRequest request
    ) {
        RecursoContenido cambios = recursoContenidoMapper.toEntity(request);
        return ResponseEntity.ok(recursoContenidoMapper.toResponse(
                recursoContenidoService.update(id, cambios)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recursoContenidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
