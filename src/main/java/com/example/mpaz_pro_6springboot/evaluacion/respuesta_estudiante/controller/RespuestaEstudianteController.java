package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.controller;

import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.request.RespuestaEstudianteCreateRequest;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.dto.response.RespuestaEstudianteResponse;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.mapper.RespuestaEstudianteMapper;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.model.RespuestaEstudiante;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.service.RespuestaEstudianteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/intentos/{intentoId}/respuestas")
@RequiredArgsConstructor
@Validated
public class RespuestaEstudianteController {

    private final RespuestaEstudianteService respuestaEstudianteService;
    private final RespuestaEstudianteMapper respuestaEstudianteMapper;

    @GetMapping
    public ResponseEntity<List<RespuestaEstudianteResponse>> findByIntentoId(
            @PathVariable @Positive Long intentoId
    ) {
        return ResponseEntity.ok(respuestaEstudianteService.findByIntentoId(intentoId).stream()
                .map(respuestaEstudianteMapper::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<RespuestaEstudianteResponse> create(
            @PathVariable @Positive Long intentoId,
            @Valid @RequestBody RespuestaEstudianteCreateRequest request
    ) {
        RespuestaEstudiante entity = respuestaEstudianteMapper.toEntity(request);
        RespuestaEstudianteResponse created = respuestaEstudianteMapper.toResponse(
                respuestaEstudianteService.create(entity, intentoId, request.preguntaId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(created);
    }
}
