package com.example.mpaz_pro_6springboot.curriculo.service;

import java.time.Instant;
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
 * este servicio no recibe ni persiste una unidad propia.</p>
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

    public List<Actividad> listarHabilitadasPorContenido(Long contenidoId) {
        return actividadRepository.findAllByContenidoIdAndHabilitadaTrue(contenidoId);
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
        validarFechas(datos.getFechaInicio(), datos.getFechaTermino());
        validarMaxIntentos(datos.getMaxIntentos());

        datos.setId(null);
        datos.setInstrucciones(instrucciones);
        datos.setTipoActividad(tipo);
        datos.setContenido(contenido);
        datos.setEstado(estado);
        return actividadRepository.save(datos);
    }

    /**
     * Actualiza los datos editables. El flag {@code habilitada} se cambia con
     * {@link #cambiarHabilitada(Long, boolean)}.
     */
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
        if (datos.getFechaInicio() != null) {
            actividad.setFechaInicio(datos.getFechaInicio());
        }
        if (datos.getFechaTermino() != null) {
            actividad.setFechaTermino(datos.getFechaTermino());
        }
        if (datos.getMaxIntentos() != null) {
            validarMaxIntentos(datos.getMaxIntentos());
            actividad.setMaxIntentos(datos.getMaxIntentos());
        }

        validarFechas(actividad.getFechaInicio(), actividad.getFechaTermino());
        return actividad;
    }

    @Transactional
    public Actividad cambiarHabilitada(Long id, boolean habilitada) {
        Actividad actividad = obtener(id);
        actividad.setHabilitada(habilitada);
        return actividad;
    }

    @Transactional
    public void eliminar(Long id) {
        actividadRepository.delete(obtener(id));
    }

    private void validarFechas(Instant inicio, Instant termino) {
        if (inicio != null && termino != null && termino.isBefore(inicio)) {
            throw new ReglaNegocioException(
                    "La fecha de termino no puede ser anterior a la fecha de inicio");
        }
    }

    private void validarMaxIntentos(Integer maxIntentos) {
        if (maxIntentos != null && maxIntentos < 0) {
            throw new ReglaNegocioException("maxIntentos no puede ser negativo");
        }
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
