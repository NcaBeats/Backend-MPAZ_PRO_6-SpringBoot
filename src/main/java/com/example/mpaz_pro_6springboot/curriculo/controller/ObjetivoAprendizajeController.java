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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.ObjetivoAprendizajeMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.service.ObjetivoAprendizajeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link ObjetivoAprendizaje}.
 *
 * <p>El codigo no es unico en el esquema, por eso el filtro por codigo devuelve
 * una lista.</p>
 */
@RestController
@RequestMapping("/api/objetivos-aprendizaje")
@RequiredArgsConstructor
@Transactional
public class ObjetivoAprendizajeController {

    private final ObjetivoAprendizajeService objetivoAprendizajeService;
    private final ObjetivoAprendizajeMapper objetivoAprendizajeMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ObjetivoAprendizajeResponse> listar(
            @RequestParam(required = false) String codigo) {
        List<ObjetivoAprendizaje> objetivos = codigo != null
                ? objetivoAprendizajeService.listarPorCodigo(codigo)
                : objetivoAprendizajeService.listar();
        return objetivoAprendizajeMapper.toResponseList(objetivos);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ObjetivoAprendizajeResponse obtener(@PathVariable Long id) {
        return objetivoAprendizajeMapper.toResponse(objetivoAprendizajeService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObjetivoAprendizajeResponse crear(
            @Valid @RequestBody ObjetivoAprendizajeRequest request) {
        ObjetivoAprendizaje creado = objetivoAprendizajeService.crear(
                objetivoAprendizajeMapper.toEntity(request));
        return objetivoAprendizajeMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public ObjetivoAprendizajeResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody ObjetivoAprendizajeUpdateRequest request) {
        ObjetivoAprendizaje actualizado = objetivoAprendizajeService.actualizar(
                id, objetivoAprendizajeMapper.toEntity(request));
        return objetivoAprendizajeMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        objetivoAprendizajeService.eliminar(id);
    }
}
