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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.AlternativaPreguntaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AlternativaPreguntaResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.AlternativaPreguntaMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.AlternativaPregunta;
import com.example.mpaz_pro_6springboot.curriculo.service.AlternativaPreguntaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de la relacion {@link AlternativaPregunta}.
 *
 * <p>La pareja pregunta-alternativa se crea o se elimina; no hay update.</p>
 */
@RestController
@RequestMapping("/api/alternativas-pregunta")
@RequiredArgsConstructor
@Transactional
public class AlternativaPreguntaController {

    private final AlternativaPreguntaService alternativaPreguntaService;
    private final AlternativaPreguntaMapper alternativaPreguntaMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<AlternativaPreguntaResponse> listar(
            @RequestParam(required = false) Long preguntaId,
            @RequestParam(required = false) Long alternativaId) {
        List<AlternativaPregunta> relaciones;
        if (preguntaId != null) {
            relaciones = alternativaPreguntaService.listarPorPregunta(preguntaId);
        } else if (alternativaId != null) {
            relaciones = alternativaPreguntaService.listarPorAlternativa(alternativaId);
        } else {
            relaciones = alternativaPreguntaService.listar();
        }
        return alternativaPreguntaMapper.toResponseList(relaciones);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AlternativaPreguntaResponse obtener(@PathVariable Long id) {
        return alternativaPreguntaMapper.toResponse(alternativaPreguntaService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlternativaPreguntaResponse crear(
            @Valid @RequestBody AlternativaPreguntaRequest request) {
        AlternativaPregunta creada = alternativaPreguntaService.crear(
                request.preguntaId(), request.alternativaId());
        return alternativaPreguntaMapper.toResponse(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        alternativaPreguntaService.eliminar(id);
    }
}
