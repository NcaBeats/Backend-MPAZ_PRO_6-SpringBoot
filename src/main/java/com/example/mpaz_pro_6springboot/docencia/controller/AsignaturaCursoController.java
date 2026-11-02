package com.example.mpaz_pro_6springboot.docencia.controller;

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

import com.example.mpaz_pro_6springboot.docencia.dto.request.AsignaturaCursoRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.response.AsignaturaCursoResponse;
import com.example.mpaz_pro_6springboot.docencia.mapper.AsignaturaCursoMapper;
import com.example.mpaz_pro_6springboot.docencia.model.AsignaturaCurso;
import com.example.mpaz_pro_6springboot.docencia.service.AsignaturaCursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de la relacion {@link AsignaturaCurso}.
 */
@RestController
@RequestMapping("/api/asignaturas-curso")
@RequiredArgsConstructor
@Transactional
public class AsignaturaCursoController {

    private final AsignaturaCursoService asignaturaCursoService;
    private final AsignaturaCursoMapper asignaturaCursoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<AsignaturaCursoResponse> listar(
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) Long asignaturaId) {
        List<AsignaturaCurso> relaciones;
        if (cursoId != null) {
            relaciones = asignaturaCursoService.listarPorCurso(cursoId);
        } else if (asignaturaId != null) {
            relaciones = asignaturaCursoService.listarPorAsignatura(asignaturaId);
        } else {
            relaciones = asignaturaCursoService.listar();
        }
        return asignaturaCursoMapper.toResponseList(relaciones);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AsignaturaCursoResponse obtener(@PathVariable Long id) {
        return asignaturaCursoMapper.toResponse(asignaturaCursoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AsignaturaCursoResponse crear(
            @Valid @RequestBody AsignaturaCursoRequest request) {
        AsignaturaCurso creada = asignaturaCursoService.crear(
                asignaturaCursoMapper.toEntity(request));
        return asignaturaCursoMapper.toResponse(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        asignaturaCursoService.eliminar(id);
    }
}
