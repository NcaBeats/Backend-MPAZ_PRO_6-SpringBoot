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

import com.example.mpaz_pro_6springboot.curriculo.dto.request.AlternativaRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.AlternativaUpdateRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AlternativaResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.AlternativaMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.service.AlternativaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link Alternativa}.
 *
 * <p>{@code correcta} se define al crear y se cambia con
 * {@code PUT /{id}/correcta}; el update generico no lo modifica.</p>
 */
@RestController
@RequestMapping("/api/alternativas")
@RequiredArgsConstructor
@Transactional
public class AlternativaController {

    private final AlternativaService alternativaService;
    private final AlternativaMapper alternativaMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<AlternativaResponse> listar(
            @RequestParam(required = false) Long recursoId) {
        List<Alternativa> alternativas = recursoId != null
                ? alternativaService.listarPorRecurso(recursoId)
                : alternativaService.listar();
        return alternativaMapper.toResponseList(alternativas);
    }

    @GetMapping("/correctas")
    @Transactional(readOnly = true)
    public List<AlternativaResponse> listarCorrectas(@RequestParam Long recursoId) {
        return alternativaMapper.toResponseList(
                alternativaService.listarCorrectasPorRecurso(recursoId));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AlternativaResponse obtener(@PathVariable Long id) {
        return alternativaMapper.toResponse(alternativaService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlternativaResponse crear(@Valid @RequestBody AlternativaRequest request) {
        Alternativa creada = alternativaService.crear(alternativaMapper.toEntity(request));
        return alternativaMapper.toResponse(creada);
    }

    @PutMapping("/{id}")
    public AlternativaResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody AlternativaUpdateRequest request) {
        Alternativa actualizada = alternativaService.actualizar(
                id, alternativaMapper.toEntity(request));
        return alternativaMapper.toResponse(actualizada);
    }

    @PutMapping("/{id}/correcta")
    public AlternativaResponse cambiarCorrecta(@PathVariable Long id,
            @RequestParam boolean correcta) {
        return alternativaMapper.toResponse(
                alternativaService.cambiarCorrecta(id, correcta));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        alternativaService.eliminar(id);
    }
}
