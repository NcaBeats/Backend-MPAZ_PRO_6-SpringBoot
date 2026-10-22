package com.example.mpaz_pro_6springboot.curriculo.alternativa.controller;

import com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.request.AlternativaCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.request.AlternativaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.dto.response.AlternativaResponse;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.mapper.AlternativaMapper;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.service.AlternativaService;
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
@RequestMapping("/api/alternativas")
@RequiredArgsConstructor
@Validated
public class AlternativaController {

    private final AlternativaService alternativaService;
    private final AlternativaMapper alternativaMapper;

    @GetMapping
    public ResponseEntity<List<AlternativaResponse>> findAll(
            @RequestParam(required = false) @Positive Long preguntaId
    ) {
        return ResponseEntity.ok(alternativaService.findAll(preguntaId).stream()
                .map(alternativaMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlternativaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alternativaMapper.toResponse(alternativaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<AlternativaResponse> create(@Valid @RequestBody AlternativaCreateRequest request) {
        Alternativa alternativa = alternativaMapper.toEntity(request);
        AlternativaResponse created = alternativaMapper.toResponse(
                alternativaService.create(alternativa, request.preguntaId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlternativaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AlternativaUpdateRequest request
    ) {
        Alternativa cambios = alternativaMapper.toEntity(request);
        return ResponseEntity.ok(alternativaMapper.toResponse(alternativaService.update(id, cambios)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alternativaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
