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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoRecursoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoRecursoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.TipoRecursoResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.TipoRecursoMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoRecurso;
import com.example.mpaz_pro_6springboot.curriculo.service.TipoRecursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST del catalogo {@link TipoRecurso}.
 */
@RestController
@RequestMapping("/api/tipos-recurso")
@RequiredArgsConstructor
@Transactional
public class TipoRecursoController {

    private final TipoRecursoService tipoRecursoService;
    private final TipoRecursoMapper tipoRecursoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<TipoRecursoResponse> listar() {
        return tipoRecursoMapper.toResponseList(tipoRecursoService.listar());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public TipoRecursoResponse obtener(@PathVariable Long id) {
        return tipoRecursoMapper.toResponse(tipoRecursoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoRecursoResponse crear(@Valid @RequestBody TipoRecursoRequest request) {
        TipoRecurso creado = tipoRecursoService.crear(request.nombre());
        return tipoRecursoMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public TipoRecursoResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody TipoRecursoUpdateRequest request) {
        TipoRecurso actualizado = tipoRecursoService.actualizar(id, request.nombre());
        return tipoRecursoMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        tipoRecursoService.eliminar(id);
    }
}
