package com.example.mpaz_pro_6springboot.informe.controller;

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

import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.informe.dto.request.DetalleInformePedagogicoRequest;
import com.example.mpaz_pro_6springboot.informe.dto.request.DetalleInformePedagogicoUpdateRequest;
import com.example.mpaz_pro_6springboot.informe.dto.response.DetalleInformePedagogicoResponse;
import com.example.mpaz_pro_6springboot.informe.mapper.DetalleInformePedagogicoMapper;
import com.example.mpaz_pro_6springboot.informe.model.DetalleInformePedagogico;
import com.example.mpaz_pro_6springboot.informe.service.DetalleInformePedagogicoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de {@link DetalleInformePedagogico}.
 *
 * <p>El listado exige {@code informePedagogicoId} o
 * {@code objetivoAprendizajeId}. El update solo permite corregir los conteos.</p>
 */
@RestController
@RequestMapping("/api/detalles-informe-pedagogico")
@RequiredArgsConstructor
@Transactional
public class DetalleInformePedagogicoController {

    private final DetalleInformePedagogicoService detalleInformePedagogicoService;
    private final DetalleInformePedagogicoMapper detalleInformePedagogicoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<DetalleInformePedagogicoResponse> listar(
            @RequestParam(required = false) Long informePedagogicoId,
            @RequestParam(required = false) Long objetivoAprendizajeId) {
        List<DetalleInformePedagogico> detalles;
        if (informePedagogicoId != null) {
            detalles = detalleInformePedagogicoService.listarPorInforme(
                    informePedagogicoId);
        } else if (objetivoAprendizajeId != null) {
            detalles = detalleInformePedagogicoService.listarPorObjetivo(
                    objetivoAprendizajeId);
        } else {
            throw new ReglaNegocioException(
                    "Debe indicar informePedagogicoId o objetivoAprendizajeId");
        }
        return detalleInformePedagogicoMapper.toResponseList(detalles);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public DetalleInformePedagogicoResponse obtener(@PathVariable Long id) {
        return detalleInformePedagogicoMapper.toResponse(
                detalleInformePedagogicoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleInformePedagogicoResponse crear(
            @Valid @RequestBody DetalleInformePedagogicoRequest request) {
        DetalleInformePedagogico creado = detalleInformePedagogicoService.crear(
                detalleInformePedagogicoMapper.toEntity(request));
        return detalleInformePedagogicoMapper.toResponse(creado);
    }

    @PutMapping("/{id}")
    public DetalleInformePedagogicoResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody DetalleInformePedagogicoUpdateRequest request) {
        DetalleInformePedagogico actualizado = detalleInformePedagogicoService.actualizar(
                id, detalleInformePedagogicoMapper.toEntity(request));
        return detalleInformePedagogicoMapper.toResponse(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        detalleInformePedagogicoService.eliminar(id);
    }
}
