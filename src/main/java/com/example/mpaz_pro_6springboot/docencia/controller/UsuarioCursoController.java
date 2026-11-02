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

import com.example.mpaz_pro_6springboot.docencia.dto.request.UsuarioCursoRequest;
import com.example.mpaz_pro_6springboot.docencia.dto.response.UsuarioCursoResponse;
import com.example.mpaz_pro_6springboot.docencia.mapper.UsuarioCursoMapper;
import com.example.mpaz_pro_6springboot.docencia.model.UsuarioCurso;
import com.example.mpaz_pro_6springboot.docencia.service.UsuarioCursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de la relacion {@link UsuarioCurso}.
 *
 * <p>La tabla no distingue rol ni tipo de participacion; esa regla queda
 * pendiente en el servicio.</p>
 */
@RestController
@RequestMapping("/api/usuarios-curso")
@RequiredArgsConstructor
@Transactional
public class UsuarioCursoController {

    private final UsuarioCursoService usuarioCursoService;
    private final UsuarioCursoMapper usuarioCursoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<UsuarioCursoResponse> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long cursoId) {
        List<UsuarioCurso> relaciones;
        if (usuarioId != null) {
            relaciones = usuarioCursoService.listarPorUsuario(usuarioId);
        } else if (cursoId != null) {
            relaciones = usuarioCursoService.listarPorCurso(cursoId);
        } else {
            relaciones = usuarioCursoService.listar();
        }
        return usuarioCursoMapper.toResponseList(relaciones);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public UsuarioCursoResponse obtener(@PathVariable Long id) {
        return usuarioCursoMapper.toResponse(usuarioCursoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioCursoResponse crear(@Valid @RequestBody UsuarioCursoRequest request) {
        UsuarioCurso creada = usuarioCursoService.crear(
                usuarioCursoMapper.toEntity(request));
        return usuarioCursoMapper.toResponse(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioCursoService.eliminar(id);
    }
}
