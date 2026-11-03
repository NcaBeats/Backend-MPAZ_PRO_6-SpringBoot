package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.EstadoAutorizacionRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.TipoActividadRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Actividad}.
 *
 * <p>La unidad de la actividad se hereda de {@code contenido.unidad}; por eso
 * este servicio no recibe ni persiste una unidad propia. Las fechas, la
 * habilitacion y el limite de intentos son configuracion por curso y viven en
 * {@code actividad_curso}.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final TipoActividadRepository tipoActividadRepository;
    private final ContenidoRepository contenidoRepository;
    private final EstadoAutorizacionRepository estadoAutorizacionRepository;

    public List<Actividad> listar() {
        return actividadRepository.findAll();
    }

    public List<Actividad> listarPorContenido(Long contenidoId) {
        return actividadRepository.findAllByContenidoId(contenidoId);
    }

    public List<Actividad> listarPorUnidad(Long unidadId) {
        return actividadRepository.findAllByContenidoUnidadId(unidadId);
    }

    public List<Actividad> listarPorEstado(Long estadoId) {
        return actividadRepository.findAllByEstadoId(estadoId);
    }

    public Actividad obtener(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", id));
    }

    @Transactional
    public Actividad crear(Actividad datos) {
        Validaciones.requerido(datos, "actividad");

        String instrucciones = Validaciones.textoObligatorio(datos.getInstrucciones(), "instrucciones");
        TipoActividad tipo = resolverTipoActividad(datos.getTipoActividad());
        Contenido contenido = resolverContenido(datos.getContenido());
        EstadoAutorizacion estado = resolverEstado(datos.getEstado());

        datos.setId(null);
        datos.setInstrucciones(instrucciones);
        datos.setTipoActividad(tipo);
        datos.setContenido(contenido);
        datos.setEstado(estado);
        return actividadRepository.save(datos);
    }

    @Transactional
    public Actividad actualizar(Long id, Actividad datos) {
        Validaciones.requerido(datos, "actividad");
        Actividad actividad = obtener(id);

        if (datos.getInstrucciones() != null) {
            actividad.setInstrucciones(
                    Validaciones.textoObligatorio(datos.getInstrucciones(), "instrucciones"));
        }
        if (datos.getTipoActividad() != null) {
            actividad.setTipoActividad(resolverTipoActividad(datos.getTipoActividad()));
        }
        if (datos.getContenido() != null) {
            actividad.setContenido(resolverContenido(datos.getContenido()));
        }
        if (datos.getEstado() != null) {
            actividad.setEstado(resolverEstado(datos.getEstado()));
        }
        return actividad;
    }

    @Transactional
    public void eliminar(Long id) {
        actividadRepository.delete(obtener(id));
    }

    private TipoActividad resolverTipoActividad(TipoActividad tipo) {
        if (tipo == null || tipo.getId() == null) {
            throw new ReglaNegocioException("El campo tipoActividad es obligatorio");
        }
        return tipoActividadRepository.findById(tipo.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "TipoActividad", tipo.getId()));
    }

    private Contenido resolverContenido(Contenido contenido) {
        if (contenido == null || contenido.getId() == null) {
            throw new ReglaNegocioException("El campo contenido es obligatorio");
        }
        return contenidoRepository.findById(contenido.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contenido", contenido.getId()));
    }

    private EstadoAutorizacion resolverEstado(EstadoAutorizacion estado) {
        if (estado == null || estado.getId() == null) {
            throw new ReglaNegocioException("El campo estado es obligatorio");
        }
        return estadoAutorizacionRepository.findById(estado.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "EstadoAutorizacion", estado.getId()));
    }
}
