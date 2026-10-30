package com.example.mpaz_pro_6springboot.curriculo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoRecurso;
import com.example.mpaz_pro_6springboot.curriculo.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.RecursoRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.TipoRecursoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link Recurso}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecursoService {

    private final RecursoRepository recursoRepository;
    private final ContenidoRepository contenidoRepository;
    private final TipoRecursoRepository tipoRecursoRepository;

    public List<Recurso> listar() {
        return recursoRepository.findAll();
    }

    public List<Recurso> listarPorContenido(Long contenidoId) {
        return recursoRepository.findAllByContenidoId(contenidoId);
    }

    public List<Recurso> listarPorTipo(Long tipoRecursoId) {
        return recursoRepository.findAllByTipoRecursoId(tipoRecursoId);
    }

    public List<Recurso> listarPorContenidoYTipo(Long contenidoId, Long tipoRecursoId) {
        return recursoRepository.findAllByContenidoIdAndTipoRecursoId(
                contenidoId, tipoRecursoId);
    }

    public Recurso obtener(Long id) {
        return recursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso", id));
    }

    @Transactional
    public Recurso crear(Recurso datos) {
        Validaciones.requerido(datos, "recurso");

        Contenido contenido = resolverContenido(datos.getContenido());
        TipoRecurso tipo = resolverTipoRecurso(datos.getTipoRecurso());

        datos.setId(null);
        datos.setNombre(Validaciones.textoObligatorio(datos.getNombre(), "nombre"));
        datos.setUrl(Validaciones.textoOpcional(datos.getUrl()));
        datos.setDescripcion(Validaciones.textoOpcional(datos.getDescripcion()));
        datos.setContenido(contenido);
        datos.setTipoRecurso(tipo);
        return recursoRepository.save(datos);
    }

    @Transactional
    public Recurso actualizar(Long id, Recurso datos) {
        Validaciones.requerido(datos, "recurso");
        Recurso recurso = obtener(id);

        if (datos.getNombre() != null) {
            recurso.setNombre(Validaciones.textoObligatorio(datos.getNombre(), "nombre"));
        }
        if (datos.getUrl() != null) {
            recurso.setUrl(Validaciones.textoOpcional(datos.getUrl()));
        }
        if (datos.getDescripcion() != null) {
            recurso.setDescripcion(Validaciones.textoOpcional(datos.getDescripcion()));
        }
        if (datos.getContenido() != null) {
            recurso.setContenido(resolverContenido(datos.getContenido()));
        }
        if (datos.getTipoRecurso() != null) {
            recurso.setTipoRecurso(resolverTipoRecurso(datos.getTipoRecurso()));
        }
        return recurso;
    }

    @Transactional
    public void eliminar(Long id) {
        recursoRepository.delete(obtener(id));
    }

    private Contenido resolverContenido(Contenido contenido) {
        if (contenido == null || contenido.getId() == null) {
            throw new ReglaNegocioException("El campo contenido es obligatorio");
        }
        return contenidoRepository.findById(contenido.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contenido", contenido.getId()));
    }

    private TipoRecurso resolverTipoRecurso(TipoRecurso tipo) {
        if (tipo == null || tipo.getId() == null) {
            throw new ReglaNegocioException("El campo tipoRecurso es obligatorio");
        }
        return tipoRecursoRepository.findById(tipo.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("TipoRecurso", tipo.getId()));
    }
}
