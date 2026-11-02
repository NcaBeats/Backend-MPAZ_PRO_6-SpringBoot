package com.example.mpaz_pro_6springboot.curriculo.controller;

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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.PreguntaActividadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.PreguntaActividadResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.PreguntaActividadMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.PreguntaActividad;
import com.example.mpaz_pro_6springboot.curriculo.service.PreguntaActividadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de la relacion {@link PreguntaActividad}.
 */
@RestController
@RequestMapping("/api/preguntas-actividad")
@RequiredArgsConstructor
@Transactional
public class PreguntaActividadController {

    private final PreguntaActividadService preguntaActividadService;
    private final PreguntaActividadMapper preguntaActividadMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<PreguntaActividadResponse> listar(
            @RequestParam(required = false) Long actividadId,
            @RequestParam(required = false) Long preguntaId) {
        List<PreguntaActividad> relaciones;
        if (actividadId != null) {
            relaciones = preguntaActividadService.listarPorActividad(actividadId);
        } else if (preguntaId != null) {
            relaciones = preguntaActividadService.listarPorPregunta(preguntaId);
        } else {
            relaciones = preguntaActividadService.listar();
        }
        return preguntaActividadMapper.toResponseList(relaciones);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PreguntaActividadResponse obtener(@PathVariable Long id) {
        return preguntaActividadMapper.toResponse(preguntaActividadService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaActividadResponse crear(
            @Valid @RequestBody PreguntaActividadRequest request) {
        PreguntaActividad creada = preguntaActividadService.crear(
                request.actividadId(), request.preguntaId());
        return preguntaActividadMapper.toResponse(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        preguntaActividadService.eliminar(id);
    }
}
