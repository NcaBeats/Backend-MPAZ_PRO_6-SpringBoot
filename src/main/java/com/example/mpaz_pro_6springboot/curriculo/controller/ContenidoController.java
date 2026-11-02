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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.ContenidoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ContenidoUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.ContenidoMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.service.ContenidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Contenido}.
 *
 * <p>La unidad es inmutable. Si el update incluye un {@code unidadId} distinto,
 * el servicio responde {@code 400}.</p>
 */
@RestController
@RequestMapping("/api/contenidos")
@RequiredArgsConstructor
@Transactional
public class ContenidoController {

    private final ContenidoService contenidoService;
    private final ContenidoMapper contenidoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ContenidoResponse> listar(
            @RequestParam(required = false) Long unidadId,
            @RequestParam(required = false) Long estadoId) {
        List<Contenido> contenidos;
        if (unidadId != null && estadoId != null) {
            contenidos = contenidoService.listarPorUnidadYEstado(unidadId, estadoId);
        } else if (unidadId != null) {
            contenidos = contenidoService.listarPorUnidad(unidadId);
        } else if (estadoId != null) {
            contenidos = contenidoService.listarPorEstado(estadoId);
        } else {
            contenidos = contenidoService.listar();
        }
        return contenidoMapper.toResponseList(contenidos);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ContenidoResponse obtener(@PathVariable Long id) {
        return contenidoMapper.toResponse(contenidoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContenidoResponse crear(@Valid @RequestBody ContenidoRequest request) {
        Contenido creado = contenidoService.crear(contenidoMapper.toEntity(request));
        return contenidoMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public ContenidoResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody ContenidoUpdateRequest request) {
        Contenido actualizado = contenidoService.actualizar(
                id, contenidoMapper.toEntity(request));
        return contenidoMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        contenidoService.eliminar(id);
    }
}
