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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoActividadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.TipoActividadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.TipoActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.TipoActividadMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.service.TipoActividadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST del catalogo {@link TipoActividad}.
 */
@RestController
@RequestMapping("/api/tipos-actividad")
@RequiredArgsConstructor
@Transactional
public class TipoActividadController {

    private final TipoActividadService tipoActividadService;
    private final TipoActividadMapper tipoActividadMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<TipoActividadResponse> listar() {
        return tipoActividadMapper.toResponseList(tipoActividadService.listar());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public TipoActividadResponse obtener(@PathVariable Long id) {
        return tipoActividadMapper.toResponse(tipoActividadService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoActividadResponse crear(@Valid @RequestBody TipoActividadRequest request) {
        TipoActividad creado = tipoActividadService.crear(request.nombre());
        return tipoActividadMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public TipoActividadResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody TipoActividadUpdateRequest request) {
        TipoActividad actualizado = tipoActividadService.actualizar(id, request.nombre());
        return tipoActividadMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        tipoActividadService.eliminar(id);
    }
}
