package com.example.mpaz_pro_6springboot.informe.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.example.mpaz_pro_6springboot.informe.dto.request.GenerarInformeRequest;
import com.example.mpaz_pro_6springboot.informe.dto.request.InformePedagogicoRequest;
import com.example.mpaz_pro_6springboot.informe.dto.request.InformePedagogicoUpdateRequest;
import com.example.mpaz_pro_6springboot.informe.dto.response.InformePedagogicoDetalladoResponse;
import com.example.mpaz_pro_6springboot.informe.dto.response.InformePedagogicoResponse;
import com.example.mpaz_pro_6springboot.informe.dto.response.InformePreliminarResponse;
import com.example.mpaz_pro_6springboot.informe.mapper.InformePedagogicoMapper;
import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.service.InformePedagogicoService;
import com.example.mpaz_pro_6springboot.informe.service.InformePreliminarService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link InformePedagogico}.
 *
 * <p>La unidad del informe no se puede cambiar; el update solo admite fecha y
 * descripcion.</p>
 */
@RestController
@RequestMapping("/api/informes-pedagogicos")
@RequiredArgsConstructor
@Transactional
public class InformePedagogicoController {

    private final InformePedagogicoService informePedagogicoService;
    private final InformePreliminarService informePreliminarService;
    private final InformePedagogicoMapper informePedagogicoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<InformePedagogicoResponse> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long unidadId) {
        List<InformePedagogico> informes;
        if (usuarioId != null && unidadId != null) {
            informes = informePedagogicoService.listarPorUsuarioYUnidad(usuarioId, unidadId);
        } else if (usuarioId != null) {
            informes = informePedagogicoService.listarPorUsuario(usuarioId);
        } else if (unidadId != null) {
            informes = informePedagogicoService.listarPorUnidad(unidadId);
        } else {
            informes = informePedagogicoService.listar();
        }
        return informePedagogicoMapper.toResponseList(informes);
    }

    /**
     * Consolidado efimero del desempeno del estudiante en la unidad, calculado
     * desde el ultimo intento cerrado de cada actividad.
     */
    @GetMapping("/preliminar")
    @Transactional(readOnly = true)
    public InformePreliminarResponse previsualizar(
            @RequestParam Long usuarioId,
            @RequestParam Long unidadId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaCorte) {
        return informePreliminarService.previsualizar(usuarioId, unidadId, fechaCorte);
    }

    /**
     * Crea el informe pedagogico a partir de los resultados preliminares. Los
     * conteos quedan persistidos como snapshot y no se recalculan despues.
     */
    @PostMapping("/desde-resultados")
    @ResponseStatus(HttpStatus.CREATED)
    public InformePedagogicoDetalladoResponse generarDesdeResultados(
            @Valid @RequestBody GenerarInformeRequest request) {
        return informePreliminarService.generarDesdeResultados(request);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public InformePedagogicoResponse obtener(@PathVariable Long id) {
        return informePedagogicoMapper.toResponse(informePedagogicoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InformePedagogicoResponse crear(
            @Valid @RequestBody InformePedagogicoRequest request) {
        InformePedagogico creado = informePedagogicoService.crear(
                informePedagogicoMapper.toEntity(request));
        return informePedagogicoMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public InformePedagogicoResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody InformePedagogicoUpdateRequest request) {
        InformePedagogico actualizado = informePedagogicoService.actualizar(
                id, informePedagogicoMapper.toEntity(request));
        return informePedagogicoMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        informePedagogicoService.eliminar(id);
    }
}
