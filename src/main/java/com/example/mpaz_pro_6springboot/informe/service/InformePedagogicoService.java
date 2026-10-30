package com.example.mpaz_pro_6springboot.informe.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.RecursoNoEncontradoException;
import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.util.Validaciones;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.repository.UsuarioRepository;
import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.repository.InformePedagogicoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de {@link InformePedagogico}.
 *
 * <p>El informe se emite en el contexto de una unidad y solo para usuarios con
 * rol {@code ESTUDIANTE}. La coherencia de los OA del detalle se valida en
 * {@link DetalleInformePedagogicoService}.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InformePedagogicoService {

    private static final String ROL_ESTUDIANTE = "ESTUDIANTE";

    private final InformePedagogicoRepository informePedagogicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadRepository unidadRepository;

    public List<InformePedagogico> listar() {
        return informePedagogicoRepository.findAll();
    }

    public List<InformePedagogico> listarPorUsuario(Long usuarioId) {
        return informePedagogicoRepository.findAllByUsuarioId(usuarioId);
    }

    public List<InformePedagogico> listarPorUnidad(Long unidadId) {
        return informePedagogicoRepository.findAllByUnidadId(unidadId);
    }

    public List<InformePedagogico> listarPorUsuarioYUnidad(Long usuarioId, Long unidadId) {
        return informePedagogicoRepository.findAllByUsuarioIdAndUnidadId(usuarioId, unidadId);
    }

    public InformePedagogico obtener(Long id) {
        return informePedagogicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("InformePedagogico", id));
    }

    @Transactional
    public InformePedagogico crear(InformePedagogico datos) {
        Validaciones.requerido(datos, "informePedagogico");

        Usuario usuario = resolverUsuario(datos.getUsuario());
        validarEstudiante(usuario);
        Unidad unidad = resolverUnidad(datos.getUnidad());

        datos.setId(null);
        datos.setUsuario(usuario);
        datos.setUnidad(unidad);
        datos.setFecha(datos.getFecha() != null ? datos.getFecha() : Instant.now());
        return informePedagogicoRepository.save(datos);
    }

    /**
     * Actualiza solo los datos descriptivos. La unidad no se puede cambiar
     * porque los detalles ya validados quedarian incoherentes.
     */
    @Transactional
    public InformePedagogico actualizar(Long id, InformePedagogico datos) {
        Validaciones.requerido(datos, "informePedagogico");
        InformePedagogico informe = obtener(id);

        if (datos.getUnidad() != null && datos.getUnidad().getId() != null
                && !datos.getUnidad().getId().equals(informe.getUnidad().getId())) {
            throw new ReglaNegocioException("La unidad del informe pedagogico no se puede cambiar");
        }
        if (datos.getDescripcion() != null) {
            informe.setDescripcion(datos.getDescripcion());
        }
        if (datos.getFecha() != null) {
            informe.setFecha(datos.getFecha());
        }
        return informe;
    }

    @Transactional
    public void eliminar(Long id) {
        informePedagogicoRepository.delete(obtener(id));
    }

    private void validarEstudiante(Usuario usuario) {
        if (usuario.getRol() == null
                || !ROL_ESTUDIANTE.equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new ReglaNegocioException(
                    "El informe pedagogico solo puede emitirse para usuarios con rol ESTUDIANTE");
        }
    }

    private Usuario resolverUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new ReglaNegocioException("El campo usuario es obligatorio");
        }
        return usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuario.getId()));
    }

    private Unidad resolverUnidad(Unidad unidad) {
        if (unidad == null || unidad.getId() == null) {
            throw new ReglaNegocioException("El campo unidad es obligatorio");
        }
        return unidadRepository.findById(unidad.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad", unidad.getId()));
    }
}
