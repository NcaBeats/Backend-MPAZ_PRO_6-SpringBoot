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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.ActividadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ActividadUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.ActividadMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.service.ActividadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Actividad}.
 *
 * <p>La unidad se hereda del contenido: no se recibe ni se filtra por una
 * unidad propia de la actividad salvo a traves de {@code unidadId}, que el
 * servicio traduce a {@code contenido.unidad_id}.</p>
 */
@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
@Transactional
public class ActividadController {

    private final ActividadService actividadService;
    private final ActividadMapper actividadMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ActividadResponse> listar(
            @RequestParam(required = false) Long contenidoId,
            @RequestParam(required = false) Long unidadId,
            @RequestParam(required = false) Long estadoId,
            @RequestParam(required = false) Boolean habilitadas) {
        List<Actividad> actividades;
        if (contenidoId != null && Boolean.TRUE.equals(habilitadas)) {
            actividades = actividadService.listarHabilitadasPorContenido(contenidoId);
        } else if (contenidoId != null) {
            actividades = actividadService.listarPorContenido(contenidoId);
        } else if (unidadId != null) {
            actividades = actividadService.listarPorUnidad(unidadId);
        } else if (estadoId != null) {
            actividades = actividadService.listarPorEstado(estadoId);
        } else {
            actividades = actividadService.listar();
        }
        return actividadMapper.toResponseList(actividades);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ActividadResponse obtener(@PathVariable Long id) {
        return actividadMapper.toResponse(actividadService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActividadResponse crear(@Valid @RequestBody ActividadRequest request) {
        Actividad creada = actividadService.crear(actividadMapper.toEntity(request));
        return actividadMapper.toResponse(creada);
    }

    @PutMapping("/{id}")
    public ActividadResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody ActividadUpdateRequest request) {
        Actividad actualizada = actividadService.actualizar(
                id, actividadMapper.toEntity(request));
        return actividadMapper.toResponse(actualizada);
    }

    @PutMapping("/{id}/habilitada")
    public ActividadResponse cambiarHabilitada(@PathVariable Long id,
            @RequestParam boolean habilitada) {
        return actividadMapper.toResponse(
                actividadService.cambiarHabilitada(id, habilitada));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
    }
}
