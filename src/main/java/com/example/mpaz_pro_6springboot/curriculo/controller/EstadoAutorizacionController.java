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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.EstadoAutorizacionRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.EstadoAutorizacionUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.EstadoAutorizacionResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.EstadoAutorizacionMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;
import com.example.mpaz_pro_6springboot.curriculo.service.EstadoAutorizacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST del catalogo {@link EstadoAutorizacion}.
 */
@RestController
@RequestMapping("/api/estados-autorizacion")
@RequiredArgsConstructor
@Transactional
public class EstadoAutorizacionController {

    private final EstadoAutorizacionService estadoAutorizacionService;
    private final EstadoAutorizacionMapper estadoAutorizacionMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<EstadoAutorizacionResponse> listar() {
        return estadoAutorizacionMapper.toResponseList(estadoAutorizacionService.listar());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public EstadoAutorizacionResponse obtener(@PathVariable Long id) {
        return estadoAutorizacionMapper.toResponse(estadoAutorizacionService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstadoAutorizacionResponse crear(
            @Valid @RequestBody EstadoAutorizacionRequest request) {
        EstadoAutorizacion creado = estadoAutorizacionService.crear(request.nombre());
        return estadoAutorizacionMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public EstadoAutorizacionResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody EstadoAutorizacionUpdateRequest request) {
        EstadoAutorizacion actualizado = estadoAutorizacionService.actualizar(id, request.nombre());
        return estadoAutorizacionMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        estadoAutorizacionService.eliminar(id);
    }
}
