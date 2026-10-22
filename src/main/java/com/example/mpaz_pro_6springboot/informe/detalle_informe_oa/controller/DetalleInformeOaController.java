package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.controller;

import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.dto.response.DetalleInformeOaResponse;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.mapper.DetalleInformeOaMapper;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.service.DetalleInformeOaService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/informes-pedagogicos/{informeId}/objetivos-aprendizaje")
@RequiredArgsConstructor
@Validated
public class DetalleInformeOaController {

    private final DetalleInformeOaService detalleInformeOaService;
    private final DetalleInformeOaMapper detalleInformeOaMapper;

    @GetMapping
    public ResponseEntity<List<DetalleInformeOaResponse>> findByInformeId(
            @PathVariable @Positive Long informeId
    ) {
        return ResponseEntity.ok(detalleInformeOaService.findByInformeId(informeId).stream()
                .map(detalleInformeOaMapper::toResponse)
                .toList());
    }
}
