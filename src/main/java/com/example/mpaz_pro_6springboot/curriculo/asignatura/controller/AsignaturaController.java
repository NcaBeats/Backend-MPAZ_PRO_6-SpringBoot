package com.example.mpaz_pro_6springboot.curriculo.asignatura.controller;

import com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.request.AsignaturaCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.request.AsignaturaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.dto.response.AsignaturaResponse;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.mapper.AsignaturaMapper;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.service.AsignaturaService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
@RequiredArgsConstructor
public class AsignaturaController {

    private final AsignaturaService asignaturaService;
    private final AsignaturaMapper asignaturaMapper;

    @GetMapping
    public ResponseEntity<List<AsignaturaResponse>> findAll() {
        return ResponseEntity.ok(asignaturaService.findAll().stream()
                .map(asignaturaMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignaturaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(asignaturaMapper.toResponse(asignaturaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<AsignaturaResponse> create(@Valid @RequestBody AsignaturaCreateRequest request) {
        Asignatura asignatura = asignaturaMapper.toEntity(request);
        AsignaturaResponse created = asignaturaMapper.toResponse(asignaturaService.create(asignatura));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsignaturaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AsignaturaUpdateRequest request
    ) {
        Asignatura cambios = asignaturaMapper.toEntity(request);
        return ResponseEntity.ok(asignaturaMapper.toResponse(asignaturaService.update(id, cambios)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        asignaturaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
