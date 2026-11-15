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

import com.example.mpaz_pro_6springboot.evaluacion.dto.request.RespuestaRequest;
import com.example.mpaz_pro_6springboot.evaluacion.dto.response.RespuestaResponse;
import com.example.mpaz_pro_6springboot.evaluacion.mapper.RespuestaMapper;
import com.example.mpaz_pro_6springboot.evaluacion.model.Respuesta;
import com.example.mpaz_pro_6springboot.evaluacion.service.RespuestaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Respuesta}.
 *
 * <p>El servicio valida que la pregunta pertenezca a la actividad del intento
 * y que la alternativa pertenezca a la pregunta.</p>
 */
@RestController
@RequestMapping("/api/respuestas")
@RequiredArgsConstructor
@Transactional
public class RespuestaController {

    private final RespuestaService respuestaService;
    private final RespuestaMapper respuestaMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<RespuestaResponse> listar(
            @RequestParam(required = false) Long intentoId,
            @RequestParam(required = false) Long preguntaId) {
        List<Respuesta> respuestas;
        if (intentoId != null && preguntaId != null) {
            respuestas = respuestaService.listarPorIntentoYPregunta(intentoId, preguntaId);
        } else if (intentoId != null) {
            respuestas = respuestaService.listarPorIntento(intentoId);
        } else if (preguntaId != null) {
            respuestas = respuestaService.listarPorPregunta(preguntaId);
        } else {
            respuestas = respuestaService.listar();
        }
        return respuestaMapper.toResponseList(respuestas);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public RespuestaResponse obtener(@PathVariable Long id) {
        return respuestaMapper.toResponse(respuestaService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaResponse crear(@Valid @RequestBody RespuestaRequest request) {
        Respuesta creada = respuestaService.crear(respuestaMapper.toEntity(request));
        return respuestaMapper.toResponse(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        respuestaService.eliminar(id);
    }
}
