package com.example.mpaz_pro_6springboot.curriculo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.curriculo.dto.request.UnidadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.UnidadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.UnidadResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.UnidadMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.service.UnidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Unidad}.
 *
 * <p>Permite filtrar por {@code asignaturaId} y {@code estadoId}; cuando ambos
 * llegan se aplica la combinacion.</p>
 */
@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
@Transactional
public class UnidadController {

    private final UnidadService unidadService;
    private final UnidadMapper unidadMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<UnidadResponse> listar(
            @RequestParam(required = false) Long asignaturaId,
            @RequestParam(required = false) Long estadoId) {
        List<Unidad> unidades;
        if (asignaturaId != null && estadoId != null) {
            unidades = unidadService.listarPorAsignaturaYEstado(asignaturaId, estadoId);
        } else if (asignaturaId != null) {
            unidades = unidadService.listarPorAsignatura(asignaturaId);
        } else if (estadoId != null) {
            unidades = unidadService.listarPorEstado(estadoId);
        } else {
            unidades = unidadService.listar();
        }
        return unidadMapper.toResponseList(unidades);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public UnidadResponse obtener(@PathVariable Long id) {
        return unidadMapper.toResponse(unidadService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnidadResponse crear(@Valid @RequestBody UnidadRequest request) {
        Unidad creada = unidadService.crear(unidadMapper.toEntity(request));
        return unidadMapper.toResponse(creada);
    }

    @PutMapping("/{id}")
    public UnidadResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody UnidadUpdateRequest request) {
        Unidad actualizada = unidadService.actualizar(id, unidadMapper.toEntity(request));
        return unidadMapper.toResponse(actualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        unidadService.eliminar(id);
    }
}
