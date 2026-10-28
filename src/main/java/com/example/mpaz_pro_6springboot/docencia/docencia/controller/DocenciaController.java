package com.example.mpaz_pro_6springboot.docencia.docencia.controller;

import com.example.mpaz_pro_6springboot.common.security.CustomUserDetails;
import com.example.mpaz_pro_6springboot.docencia.docencia.dto.request.DocenciaCreateRequest;
import com.example.mpaz_pro_6springboot.docencia.docencia.dto.request.DocenciaUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.docencia.dto.response.DocenciaResponse;
import com.example.mpaz_pro_6springboot.docencia.docencia.mapper.DocenciaMapper;
import com.example.mpaz_pro_6springboot.docencia.docencia.service.DocenciaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/docencias")
@RequiredArgsConstructor
@Validated
public class DocenciaController {

    private final DocenciaService docenciaService;
    private final DocenciaMapper docenciaMapper;

    @GetMapping
    public ResponseEntity<List<DocenciaResponse>> findAll(
            @RequestParam(required = false) @Positive Long docenteId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(docenciaService
                .findAll(docenteId, principal.getUsuario()).stream()
                .map(docenciaMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocenciaResponse> findById(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(docenciaMapper.toResponse(
                docenciaService.findById(id, principal.getUsuario())
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('UTP')")
    public ResponseEntity<DocenciaResponse> create(
            @Valid @RequestBody DocenciaCreateRequest request
    ) {
        DocenciaResponse created = docenciaMapper.toResponse(
                docenciaService.create(request)
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('UTP')")
    public ResponseEntity<DocenciaResponse> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody DocenciaUpdateRequest request
    ) {
        return ResponseEntity.ok(docenciaMapper.toResponse(
                docenciaService.update(id, request)
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('UTP')")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        docenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
