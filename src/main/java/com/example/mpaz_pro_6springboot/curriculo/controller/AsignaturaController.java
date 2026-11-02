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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.curriculo.dto.request.AsignaturaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.AsignaturaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AsignaturaResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.AsignaturaMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.service.AsignaturaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Asignatura}.
 */
@RestController
@RequestMapping("/api/asignaturas")
@RequiredArgsConstructor
@Transactional
public class AsignaturaController {

    private final AsignaturaService asignaturaService;
    private final AsignaturaMapper asignaturaMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<AsignaturaResponse> listar() {
        return asignaturaMapper.toResponseList(asignaturaService.listar());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AsignaturaResponse obtener(@PathVariable Long id) {
        return asignaturaMapper.toResponse(asignaturaService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AsignaturaResponse crear(@Valid @RequestBody AsignaturaRequest request) {
        Asignatura creada = asignaturaService.crear(request.nombre());
        return asignaturaMapper.toResponse(creada);
    }

    @PutMapping("/{id}")
    public AsignaturaResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody AsignaturaUpdateRequest request) {
        Asignatura actualizada = asignaturaService.actualizar(id, request.nombre());
        return asignaturaMapper.toResponse(actualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        asignaturaService.eliminar(id);
    }
}
