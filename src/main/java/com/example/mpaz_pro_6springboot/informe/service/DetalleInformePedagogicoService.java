package com.example.mpaz_pro_6springboot.informe.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ConflictoException;
import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeUnidadRepository;
import com.example.mpaz_pro_6springboot.informe.model.DetalleInformePedagogico;
import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.repository.DetalleInformePedagogicoRepository;
import com.example.mpaz_pro_6springboot.informe.repository.InformePedagogicoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link DetalleInformePedagogico}.
 *
 * <p>Valida que cada OA del detalle este ofrecido en la unidad del informe,
 * regla que el esquema no puede expresar porque el detalle no guarda
 * {@code unidad_id}.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleInformePedagogicoService {

    private final DetalleInformePedagogicoRepository detalleRepository;
    private final InformePedagogicoRepository informePedagogicoRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final ObjetivoAprendizajeUnidadRepository objetivoAprendizajeUnidadRepository;

    public List<DetalleInformePedagogico> listarPorInforme(Long informePedagogicoId) {
        return detalleRepository.findAllByInformePedagogicoId(informePedagogicoId);
    }

    public List<DetalleInformePedagogico> listarPorObjetivo(Long objetivoAprendizajeId) {
        return detalleRepository.findAllByObjetivoAprendizajeId(objetivoAprendizajeId);
    }

    public DetalleInformePedagogico obtener(Long id) {
        return detalleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "DetalleInformePedagogico", id));
    }

    @Transactional
    public DetalleInformePedagogico crear(DetalleInformePedagogico datos) {
        Validaciones.requerido(datos, "detalleInformePedagogico");

        InformePedagogico informe = resolverInforme(datos.getInformePedagogico());
        ObjetivoAprendizaje objetivo = resolverObjetivo(datos.getObjetivoAprendizaje());
        validarObjetivoEnUnidad(informe, objetivo);
        validarConteos(datos.getCorrectas(), datos.getIncorrectas());

        if (detalleRepository.existsByInformePedagogicoIdAndObjetivoAprendizajeId(
                informe.getId(), objetivo.getId())) {
            throw new ConflictoException(
                    "El objetivo de aprendizaje ya tiene un detalle en este informe");
        }

        datos.setId(null);
        datos.setInformePedagogico(informe);
        datos.setObjetivoAprendizaje(objetivo);
        return detalleRepository.save(datos);
    }

    @Transactional
    public DetalleInformePedagogico actualizar(Long id, DetalleInformePedagogico datos) {
        Validaciones.requerido(datos, "detalleInformePedagogico");
        DetalleInformePedagogico detalle = obtener(id);

        Integer correctas = datos.getCorrectas() != null ? datos.getCorrectas() : detalle.getCorrectas();
        Integer incorrectas = datos.getIncorrectas() != null
                ? datos.getIncorrectas()
                : detalle.getIncorrectas();
        validarConteos(correctas, incorrectas);

        detalle.setCorrectas(correctas);
        detalle.setIncorrectas(incorrectas);
        return detalle;
    }

    @Transactional
    public void eliminar(Long id) {
        detalleRepository.delete(obtener(id));
    }

    private void validarObjetivoEnUnidad(InformePedagogico informe, ObjetivoAprendizaje objetivo) {
        if (!objetivoAprendizajeUnidadRepository.existsByObjetivoAprendizajeIdAndUnidadId(
                objetivo.getId(), informe.getUnidad().getId())) {
            throw new ReglaNegocioException(
                    "El objetivo de aprendizaje no esta ofrecido en la unidad del informe");
        }
    }

    private void validarConteos(Integer correctas, Integer incorrectas) {
        if (correctas == null || incorrectas == null) {
            throw new ReglaNegocioException(
                    "Los campos correctas e incorrectas son obligatorios");
        }
        if (correctas < 0 || incorrectas < 0) {
            throw new ReglaNegocioException(
                    "Los campos correctas e incorrectas no pueden ser negativos");
        }
    }

    private InformePedagogico resolverInforme(InformePedagogico informe) {
        if (informe == null || informe.getId() == null) {
            throw new ReglaNegocioException("El campo informePedagogico es obligatorio");
        }
        return informePedagogicoRepository.findById(informe.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "InformePedagogico", informe.getId()));
    }

    private ObjetivoAprendizaje resolverObjetivo(ObjetivoAprendizaje objetivo) {
        if (objetivo == null || objetivo.getId() == null) {
            throw new ReglaNegocioException("El campo objetivoAprendizaje es obligatorio");
        }
        return objetivoAprendizajeRepository.findById(objetivo.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "ObjetivoAprendizaje", objetivo.getId()));
    }
}
