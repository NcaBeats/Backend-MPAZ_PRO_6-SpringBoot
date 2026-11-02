package com.example.mpaz_pro_6springboot.evaluacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.evaluacion.dto.request.IntentoRequest;
import com.example.mpaz_pro_6springboot.evaluacion.dto.response.IntentoResponse;
import com.example.mpaz_pro_6springboot.evaluacion.mapper.IntentoMapper;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.service.IntentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Intento}.
 *
 * <p>El cierre usa {@code POST /{id}/cerrar}; no existe update generico.</p>
 */
@RestController
@RequestMapping("/api/intentos")
@RequiredArgsConstructor
@Transactional
public class IntentoController {

    private final IntentoService intentoService;
    private final IntentoMapper intentoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<IntentoResponse> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long actividadId) {
        List<Intento> intentos;
        if (usuarioId != null && actividadId != null) {
            intentos = intentoService.listarPorUsuarioYActividad(usuarioId, actividadId);
        } else if (usuarioId != null) {
            intentos = intentoService.listarPorUsuario(usuarioId);
        } else if (actividadId != null) {
            intentos = intentoService.listarPorActividad(actividadId);
        } else {
            intentos = intentoService.listar();
        }
        return intentoMapper.toResponseList(intentos);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public IntentoResponse obtener(@PathVariable Long id) {
        return intentoMapper.toResponse(intentoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IntentoResponse crear(@Valid @RequestBody IntentoRequest request) {
        Intento creado = intentoService.crear(intentoMapper.toEntity(request));
        return intentoMapper.toResponse(creado);
    }

    @PostMapping("/{id}/cerrar")
    public IntentoResponse cerrar(@PathVariable Long id) {
        return intentoMapper.toResponse(intentoService.cerrar(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        intentoService.eliminar(id);
    }
}
