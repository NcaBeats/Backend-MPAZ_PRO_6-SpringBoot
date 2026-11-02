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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.PreguntaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.PreguntaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.PreguntaResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.PreguntaMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.service.PreguntaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Pregunta}.
 *
 * <p>Toda pregunta referencia un recurso obligatorio. El filtro por contenido
 * navega desde la pregunta hacia su recurso.</p>
 */
@RestController
@RequestMapping("/api/preguntas")
@RequiredArgsConstructor
@Transactional
public class PreguntaController {

    private final PreguntaService preguntaService;
    private final PreguntaMapper preguntaMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<PreguntaResponse> listar(
            @RequestParam(required = false) Long recursoId,
            @RequestParam(required = false) Long contenidoId) {
        List<Pregunta> preguntas;
        if (recursoId != null) {
            preguntas = preguntaService.listarPorRecurso(recursoId);
        } else if (contenidoId != null) {
            preguntas = preguntaService.listarPorContenido(contenidoId);
        } else {
            preguntas = preguntaService.listar();
        }
        return preguntaMapper.toResponseList(preguntas);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PreguntaResponse obtener(@PathVariable Long id) {
        return preguntaMapper.toResponse(preguntaService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaResponse crear(@Valid @RequestBody PreguntaRequest request) {
        Pregunta creada = preguntaService.crear(preguntaMapper.toEntity(request));
        return preguntaMapper.toResponse(creada);
    }

    @PutMapping("/{id}")
    public PreguntaResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody PreguntaUpdateRequest request) {
        Pregunta actualizada = preguntaService.actualizar(
                id, preguntaMapper.toEntity(request));
        return preguntaMapper.toResponse(actualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        preguntaService.eliminar(id);
    }
}
