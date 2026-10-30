package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link ObjetivoAprendizaje}.
 *
 * <p>{@code codigo} no es unico en el esquema: puede haber varios OA con el
 * mismo codigo, por eso las busquedas por codigo devuelven listas.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObjetivoAprendizajeService {

    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;

    public List<ObjetivoAprendizaje> listar() {
        return objetivoAprendizajeRepository.findAllByOrderByCodigoAsc();
    }

    public List<ObjetivoAprendizaje> listarPorCodigo(String codigo) {
        return objetivoAprendizajeRepository.findAllByCodigo(
                Validaciones.textoObligatorio(codigo, "codigo"));
    }

    public ObjetivoAprendizaje obtener(Long id) {
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("ObjetivoAprendizaje", id));
    }

    @Transactional
    public ObjetivoAprendizaje crear(ObjetivoAprendizaje datos) {
        Validaciones.requerido(datos, "objetivoAprendizaje");

        datos.setId(null);
        datos.setDescripcion(Validaciones.textoObligatorio(datos.getDescripcion(), "descripcion"));
        datos.setCodigo(Validaciones.textoObligatorio(datos.getCodigo(), "codigo"));
        return objetivoAprendizajeRepository.save(datos);
    }

    @Transactional
    public ObjetivoAprendizaje actualizar(Long id, ObjetivoAprendizaje datos) {
        Validaciones.requerido(datos, "objetivoAprendizaje");
        ObjetivoAprendizaje objetivo = obtener(id);

        if (datos.getDescripcion() != null) {
            objetivo.setDescripcion(
                    Validaciones.textoObligatorio(datos.getDescripcion(), "descripcion"));
        }
        if (datos.getCodigo() != null) {
            objetivo.setCodigo(Validaciones.textoObligatorio(datos.getCodigo(), "codigo"));
        }
        return objetivo;
    }

    @Transactional
    public void eliminar(Long id) {
        objetivoAprendizajeRepository.delete(obtener(id));
    }
}
