package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.controller;

import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.request.InformePedagogicoCreateRequest;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.dto.response.InformePedagogicoResponse;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.mapper.InformePedagogicoMapper;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.service.InformePedagogicoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/informes-pedagogicos")
@RequiredArgsConstructor
@Validated
public class InformePedagogicoController {

    private final InformePedagogicoService informePedagogicoService;
    private final InformePedagogicoMapper informePedagogicoMapper;

    @GetMapping
    public ResponseEntity<List<InformePedagogicoResponse>> findAll(
            @RequestParam(required = false) @Positive Long estudianteId,
            @RequestParam(required = false) @Positive Long unidadId
    ) {
        return ResponseEntity.ok(informePedagogicoService.findAll(estudianteId, unidadId).stream()
                .map(informePedagogicoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InformePedagogicoResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(informePedagogicoMapper.toResponse(informePedagogicoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<InformePedagogicoResponse> generar(
            @Valid @RequestBody InformePedagogicoCreateRequest request
    ) {
        InformePedagogicoResponse created = informePedagogicoMapper.toResponse(
                informePedagogicoService.generar(
                        request.estudianteId(),
                        request.unidadId(),
                        request.docenteId()
                )
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}/validacion")
    public ResponseEntity<InformePedagogicoResponse> validar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(informePedagogicoMapper.toResponse(informePedagogicoService.validar(id)));
    }
}
