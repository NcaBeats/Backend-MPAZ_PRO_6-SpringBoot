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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpaz_pro_6springboot.identidad.dto.request.CursoRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.request.CursoUpdateRequest;
import com.example.mpaz_pro_6springboot.identidad.dto.response.CursoResponse;
import com.example.mpaz_pro_6springboot.identidad.mapper.CursoMapper;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.service.CursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Curso}.
 *
 * <p>Los filtros de listado son excluyentes y se aplican en este orden:
 * {@code annio}, {@code nivelId} y {@code seccionId}.</p>
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Transactional
public class CursoController {

    private final CursoService cursoService;
    private final CursoMapper cursoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<CursoResponse> listar(
            @RequestParam(required = false) Integer annio,
            @RequestParam(required = false) Long nivelId,
            @RequestParam(required = false) Long seccionId) {
        List<Curso> cursos;
        if (annio != null) {
            cursos = cursoService.listarPorAnnio(annio);
        } else if (nivelId != null) {
            cursos = cursoService.listarPorNivel(nivelId);
        } else if (seccionId != null) {
            cursos = cursoService.listarPorSeccion(seccionId);
        } else {
            cursos = cursoService.listar();
        }
        return cursoMapper.toResponseList(cursos);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public CursoResponse obtener(@PathVariable Long id) {
        return cursoMapper.toResponse(cursoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoResponse crear(@Valid @RequestBody CursoRequest request) {
        Curso creado = cursoService.crear(cursoMapper.toEntity(request));
        return cursoMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public CursoResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody CursoUpdateRequest request) {
        Curso actualizado = cursoService.actualizar(id, cursoMapper.toEntity(request));
        return cursoMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
    }
}
