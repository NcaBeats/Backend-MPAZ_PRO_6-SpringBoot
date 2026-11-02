package com.example.mpaz_pro_6springboot.identidad.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.identidad.dto.request.NivelRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.NivelUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.NivelResponse;
import com.example.mpaz_pro_6springboot.identidad.mapper.NivelMapper;
import com.example.mpaz_pro_6springboot.identidad.model.Nivel;
import com.example.mpaz_pro_6springboot.identidad.service.NivelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST del catalogo {@link Nivel}.
 */
@RestController
@RequestMapping("/api/niveles")
@RequiredArgsConstructor
@Transactional
public class NivelController {

    private final NivelService nivelService;
    private final NivelMapper nivelMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<NivelResponse> listar() {
        return nivelMapper.toResponseList(nivelService.listar());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public NivelResponse obtener(@PathVariable Long id) {
        return nivelMapper.toResponse(nivelService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NivelResponse crear(@Valid @RequestBody NivelRequest request) {
        Nivel creado = nivelService.crear(request.nombre());
        return nivelMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public NivelResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody NivelUpdateRequest request) {
        Nivel actualizado = nivelService.actualizar(id, request.nombre());
        return nivelMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        nivelService.eliminar(id);
    }
}
