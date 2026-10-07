package com.example.mpaz_pro_6springboot.identidad.curso.controller;

import com.example.mpaz_pro_6springboot.identidad.curso.dto.request.CursoCreateRequest;
import com.example.mpaz_pro_6springboot.identidad.curso.dto.request.CursoUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.curso.dto.response.CursoResponse;
import com.example.mpaz_pro_6springboot.identidad.curso.mapper.CursoMapper;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.curso.service.CursoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Validated
public class CursoController {

    private final CursoService cursoService;
    private final CursoMapper cursoMapper;

    @GetMapping
    public ResponseEntity<List<CursoResponse>> findAll(
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer anio
    ) {
        return ResponseEntity.ok(cursoService.findAll(anio).stream()
                .map(cursoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cursoMapper.toResponse(cursoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<CursoResponse> create(@Valid @RequestBody CursoCreateRequest request) {
        Curso curso = cursoMapper.toEntity(request);
        CursoResponse created = cursoMapper.toResponse(cursoService.create(curso));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CursoUpdateRequest request
    ) {
        Curso cambios = cursoMapper.toEntity(request);
        return ResponseEntity.ok(cursoMapper.toResponse(cursoService.update(id, cambios)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
