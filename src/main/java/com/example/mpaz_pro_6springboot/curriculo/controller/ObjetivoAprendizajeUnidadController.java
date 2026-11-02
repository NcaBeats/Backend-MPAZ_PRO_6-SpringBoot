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

import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeUnidadRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeUnidadResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.ObjetivoAprendizajeUnidadMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeUnidad;
import com.example.mpaz_pro_6springboot.curriculo.service.ObjetivoAprendizajeUnidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de la relacion N:M {@link ObjetivoAprendizajeUnidad}.
 *
 * <p>La pareja OA-unidad se crea o se elimina; no existe actualizacion. El
 * listado exige {@code unidadId} o {@code objetivoAprendizajeId}.</p>
 */
@RestController
@RequestMapping("/api/objetivos-aprendizaje-unidad")
@RequiredArgsConstructor
@Transactional
public class ObjetivoAprendizajeUnidadController {

    private final ObjetivoAprendizajeUnidadService objetivoAprendizajeUnidadService;
    private final ObjetivoAprendizajeUnidadMapper objetivoAprendizajeUnidadMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ObjetivoAprendizajeUnidadResponse> listar(
            @RequestParam(required = false) Long unidadId,
            @RequestParam(required = false) Long objetivoAprendizajeId) {
        List<ObjetivoAprendizajeUnidad> relaciones;
        if (unidadId != null) {
            relaciones = objetivoAprendizajeUnidadService.listarPorUnidad(unidadId);
        } else if (objetivoAprendizajeId != null) {
            relaciones = objetivoAprendizajeUnidadService.listarPorObjetivo(
                    objetivoAprendizajeId);
        } else {
            throw new ReglaNegocioException(
                    "Debe indicar unidadId o objetivoAprendizajeId");
        }
        return objetivoAprendizajeUnidadMapper.toResponseList(relaciones);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ObjetivoAprendizajeUnidadResponse obtener(@PathVariable Long id) {
        return objetivoAprendizajeUnidadMapper.toResponse(
                objetivoAprendizajeUnidadService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObjetivoAprendizajeUnidadResponse crear(
            @Valid @RequestBody ObjetivoAprendizajeUnidadRequest request) {
        ObjetivoAprendizajeUnidad creada = objetivoAprendizajeUnidadService.crear(
                request.objetivoAprendizajeId(), request.unidadId());
        return objetivoAprendizajeUnidadMapper.toResponse(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        objetivoAprendizajeUnidadService.eliminar(id);
    }
}
