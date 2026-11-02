package com.example.mpaz_pro_6springboot.curriculo.controller;

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

import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.curriculo.dto.request.ObjetivoAprendizajeContenidoRequest;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeContenidoResponse;
import com.example.mpaz_pro_6springboot.curriculo.mapper.ObjetivoAprendizajeContenidoMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeContenido;
import com.example.mpaz_pro_6springboot.curriculo.service.ObjetivoAprendizajeContenidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API REST de la relacion {@link ObjetivoAprendizajeContenido}.
 *
 * <p>La unidad se toma del contenido y el OA debe estar ofrecido en ella. El
 * listado exige al menos uno de los filtros disponibles.</p>
 */
@RestController
@RequestMapping("/api/objetivos-aprendizaje-contenido")
@RequiredArgsConstructor
@Transactional
public class ObjetivoAprendizajeContenidoController {

    private final ObjetivoAprendizajeContenidoService objetivoAprendizajeContenidoService;
    private final ObjetivoAprendizajeContenidoMapper objetivoAprendizajeContenidoMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ObjetivoAprendizajeContenidoResponse> listar(
            @RequestParam(required = false) Long contenidoId,
            @RequestParam(required = false) Long objetivoAprendizajeId,
            @RequestParam(required = false) Long unidadId) {
        List<ObjetivoAprendizajeContenido> relaciones;
        if (contenidoId != null) {
            relaciones = objetivoAprendizajeContenidoService.listarPorContenido(contenidoId);
        } else if (objetivoAprendizajeId != null) {
            relaciones = objetivoAprendizajeContenidoService.listarPorObjetivo(
                    objetivoAprendizajeId);
        } else if (unidadId != null) {
            relaciones = objetivoAprendizajeContenidoService.listarPorUnidad(unidadId);
        } else {
            throw new ReglaNegocioException(
                    "Debe indicar contenidoId, objetivoAprendizajeId o unidadId");
        }
        return objetivoAprendizajeContenidoMapper.toResponseList(relaciones);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ObjetivoAprendizajeContenidoResponse obtener(@PathVariable Long id) {
        return objetivoAprendizajeContenidoMapper.toResponse(
                objetivoAprendizajeContenidoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObjetivoAprendizajeContenidoResponse crear(
            @Valid @RequestBody ObjetivoAprendizajeContenidoRequest request) {
        ObjetivoAprendizajeContenido creada = objetivoAprendizajeContenidoService.crear(
                request.objetivoAprendizajeId(), request.contenidoId());
        return objetivoAprendizajeContenidoMapper.toResponse(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        objetivoAprendizajeContenidoService.eliminar(id);
    }
}
