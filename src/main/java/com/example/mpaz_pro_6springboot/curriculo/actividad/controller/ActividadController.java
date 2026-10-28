package com.example.mpaz_pro_6springboot.curriculo.actividad.controller;

import com.example.mpaz_pro_6springboot.common.security.CustomUserDetails;
import com.example.mpaz_pro_6springboot.curriculo.actividad.dto.request.ActividadCreateRequest;
import com.example.mpaz_pro_6springboot.curriculo.actividad.dto.request.ActividadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.actividad.dto.response.ActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.actividad.mapper.ActividadMapper;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.service.ActividadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
@Validated
public class ActividadController {

    private final ActividadService actividadService;
    private final ActividadMapper actividadMapper;

    @GetMapping
    public ResponseEntity<List<ActividadResponse>> findAll(
            @RequestParam(required = false) @Positive Long unidadId,
            @RequestParam(required = false) @Positive Long contenidoId,
            @RequestParam(required = false) Boolean soloTransversales,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(actividadService
                .findAll(
                        unidadId,
                        contenidoId,
                        soloTransversales,
                        principal.getUsuario()
                )
                .stream()
                .map(actividadMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(actividadMapper.toResponse(
                actividadService.findById(id, principal.getUsuario())
        ));
    }

    @PostMapping
    public ResponseEntity<ActividadResponse> create(
            @Valid @RequestBody ActividadCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Actividad actividad = actividadMapper.toEntity(request);
        ActividadResponse created = actividadMapper.toResponse(
                actividadService.create(
                        actividad,
                        request.unidadId(),
                        request.contenidoId(),
                        principal.getUsuario()
                )
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ActividadUpdateRequest request
    ) {
        Actividad cambios = actividadMapper.toEntity(request);
        return ResponseEntity.ok(actividadMapper.toResponse(
                actividadService.update(
                        id,
                        cambios,
                        request.contenidoId(),
                        request.desasociarContenido()
                )
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        actividadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
