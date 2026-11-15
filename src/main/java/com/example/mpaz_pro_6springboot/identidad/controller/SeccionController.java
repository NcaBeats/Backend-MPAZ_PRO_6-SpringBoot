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

import com.example.mpaz_pro_6springboot.identidad.dto.request.SeccionRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.SeccionUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.SeccionResponse;
import com.example.mpaz_pro_6springboot.identidad.mapper.SeccionMapper;
import com.example.mpaz_pro_6springboot.identidad.model.Seccion;
import com.example.mpaz_pro_6springboot.identidad.service.SeccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST del catalogo {@link Seccion}.
 */
@RestController
@RequestMapping("/api/secciones")
@RequiredArgsConstructor
@Transactional
public class SeccionController {

    private final SeccionService seccionService;
    private final SeccionMapper seccionMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<SeccionResponse> listar() {
        return seccionMapper.toResponseList(seccionService.listar());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public SeccionResponse obtener(@PathVariable Long id) {
        return seccionMapper.toResponse(seccionService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeccionResponse crear(@Valid @RequestBody SeccionRequest request) {
        Seccion creada = seccionService.crear(request.letra());
        return seccionMapper.toResponse(creada);
    }

    @PutMapping("/{id}")
    public SeccionResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody SeccionUpdateRequest request) {
        Seccion actualizada = seccionService.actualizar(id, request.letra());
        return seccionMapper.toResponse(actualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        seccionService.eliminar(id);
    }
}
