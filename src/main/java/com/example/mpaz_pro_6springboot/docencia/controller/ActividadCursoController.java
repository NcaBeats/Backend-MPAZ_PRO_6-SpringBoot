package com.example.mpaz_pro_6springboot.docencia.controller;

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

import com.example.mpaz_pro_6springboot.docencia.dto.request.ActividadCursoRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.request.ActividadCursoUpdateRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.response.ActividadCursoResponse;
import com.example.mpaz_pro_6springboot.docencia.mapper.ActividadCursoMapper;
import com.example.mpaz_pro_6springboot.docencia.model.ActividadCurso;
import com.example.mpaz_pro_6springboot.docencia.service.ActividadCursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link ActividadCurso}.
 */
@RestController
@RequestMapping("/api/actividades-curso")
@RequiredArgsConstructor
@Transactional
public class ActividadCursoController {

    private final ActividadCursoService actividadCursoService;
    private final ActividadCursoMapper actividadCursoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ActividadCursoResponse> listar(
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) Long actividadId) {
        List<ActividadCurso> asignaciones;
        if (cursoId != null) {
            asignaciones = actividadCursoService.listarPorCurso(cursoId);
        } else if (actividadId != null) {
            asignaciones = actividadCursoService.listarPorActividad(actividadId);
        } else {
            asignaciones = actividadCursoService.listar();
        }
        return actividadCursoMapper.toResponseList(asignaciones);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ActividadCursoResponse obtener(@PathVariable Long id) {
        return actividadCursoMapper.toResponse(actividadCursoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActividadCursoResponse crear(@Valid @RequestBody ActividadCursoRequest request) {
        ActividadCurso creada = actividadCursoService.crear(
                actividadCursoMapper.toEntity(request));
        return actividadCursoMapper.toResponse(creada);
    }

    @PutMapping("/{id}")
    public ActividadCursoResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody ActividadCursoUpdateRequest request) {
        ActividadCurso actualizada = actividadCursoService.actualizar(
                id, actividadCursoMapper.toEntity(request));
        return actividadCursoMapper.toResponse(actualizada);
    }

    @PutMapping("/{id}/habilitada")
    public ActividadCursoResponse cambiarHabilitada(@PathVariable Long id,
            @RequestParam boolean habilitada) {
        return actividadCursoMapper.toResponse(
                actividadCursoService.cambiarHabilitada(id, habilitada));
    }

    @PutMapping("/{id}/visible")
    public ActividadCursoResponse cambiarVisible(@PathVariable Long id,
            @RequestParam boolean visible) {
        return actividadCursoMapper.toResponse(
                actividadCursoService.cambiarVisible(id, visible));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        actividadCursoService.eliminar(id);
    }
}
