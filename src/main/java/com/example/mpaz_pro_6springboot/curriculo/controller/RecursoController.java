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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.RecursoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.RecursoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.RecursoResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.RecursoMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;
import com.example.mpaz_pro_6springboot.curriculo.service.RecursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Recurso}.
 */
@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
@Transactional
public class RecursoController {

    private final RecursoService recursoService;
    private final RecursoMapper recursoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<RecursoResponse> listar(
            @RequestParam(required = false) Long contenidoId,
            @RequestParam(required = false) Long tipoRecursoId) {
        List<Recurso> recursos;
        if (contenidoId != null && tipoRecursoId != null) {
            recursos = recursoService.listarPorContenidoYTipo(contenidoId, tipoRecursoId);
        } else if (contenidoId != null) {
            recursos = recursoService.listarPorContenido(contenidoId);
        } else if (tipoRecursoId != null) {
            recursos = recursoService.listarPorTipo(tipoRecursoId);
        } else {
            recursos = recursoService.listar();
        }
        return recursoMapper.toResponseList(recursos);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public RecursoResponse obtener(@PathVariable Long id) {
        return recursoMapper.toResponse(recursoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoResponse crear(@Valid @RequestBody RecursoRequest request) {
        Recurso creado = recursoService.crear(recursoMapper.toEntity(request));
        return recursoMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public RecursoResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody RecursoUpdateRequest request) {
        Recurso actualizado = recursoService.actualizar(id, recursoMapper.toEntity(request));
        return recursoMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        recursoService.eliminar(id);
    }
}
