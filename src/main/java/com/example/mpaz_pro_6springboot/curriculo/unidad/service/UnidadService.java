package com.example.mpaz_pro_6springboot.curriculo.unidad.service;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.common.security.CurriculoAuthoringPolicy;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.repository.RecursoContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository.InformePedagogicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnidadService {

    private static final String NIVEL_EDUCATIVO_PREDETERMINADO = "Sexto Basico";
    private static final int ORDEN_PREDETERMINADO = 1;

    private final UnidadRepository unidadRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final ActividadRepository actividadRepository;
    private final ContenidoRepository contenidoRepository;
    private final RecursoContenidoRepository recursoContenidoRepository;
    private final AsignacionRepository asignacionRepository;
    private final InformePedagogicoRepository informePedagogicoRepository;
    private final CurriculoAuthoringPolicy authoringPolicy;

    public List<Unidad> findAll(Long asignaturaId) {
        if (asignaturaId == null) {
            return unidadRepository.findAllByOrderByAsignaturaIdAscOrdenAsc();
        }

        getAsignatura(asignaturaId);
        return unidadRepository.findByAsignaturaIdOrderByOrdenAsc(asignaturaId);
    }

    public Unidad findById(Long id) {
        return getUnidad(id);
    }

    @Transactional
    public Unidad create(Unidad unidad, Long asignaturaId) {
        Asignatura asignatura = getAsignatura(asignaturaId);
        if (unidadRepository.existsByAsignaturaId(asignaturaId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La asignatura ya tiene una unidad; el MVP admite máximo una por asignatura"
            );
        }
        unidad.setAsignatura(asignatura);
        if (unidad.getNivelEducativo() == null) {
            unidad.setNivelEducativo(NIVEL_EDUCATIVO_PREDETERMINADO);
        }
        if (unidad.getOrden() == null) {
            unidad.setOrden(ORDEN_PREDETERMINADO);
        }
        if (requiresAuthorization(unidad.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Una unidad autorizada requiere un usuario UTP y fecha de autorización"
            );
        }

        return unidadRepository.save(unidad);
    }

    @Transactional
    public Unidad update(Long id, Unidad cambios, Long autorizadaPorId) {
        if (isEmptyUpdate(cambios, autorizadaPorId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Unidad unidad = getUnidad(id);
        if (cambios.getNivelEducativo() != null) {
            unidad.setNivelEducativo(cambios.getNivelEducativo());
        }
        if (cambios.getTitulo() != null) {
            unidad.setTitulo(cambios.getTitulo());
        }
        if (cambios.getOrden() != null) {
            unidad.setOrden(cambios.getOrden());
        }
        if (cambios.getEstado() != null) {
            unidad.setEstado(cambios.getEstado());
        }
        if (autorizadaPorId != null) {
            unidad.setAutorizadaPor(getUsuarioUtp(autorizadaPorId));
        } else if (unidad.getAutorizadaPor() != null) {
            getUsuarioUtp(unidad.getAutorizadaPor().getId());
        }
        if (cambios.getFechaAutorizacion() != null) {
            unidad.setFechaAutorizacion(cambios.getFechaAutorizacion());
        }

        validateAuthorization(unidad);
        Unidad saved = unidadRepository.save(unidad);
        sincronizarEstadoMaterial(saved);
        return saved;
    }

    /**
     * Propaga el estado de la unidad a sus contenidos y recursos.
     *
     * El docente crea material en BORRADOR; cuando la UTP autoriza o publica,
     * ese material debe acompañar el estado de la unidad para que el estudiante
     * lo reciba. El material no se publica por una vía distinta a la unidad.
     */
    private void sincronizarEstadoMaterial(Unidad unidad) {
        if (unidad.getEstado() != EstadoContenido.AUTORIZADO
                && unidad.getEstado() != EstadoContenido.PUBLICADO) {
            return;
        }

        List<Contenido> contenidos = contenidoRepository.findByUnidadId(unidad.getId());
        contenidos.forEach(contenido -> contenido.setEstado(unidad.getEstado()));
        if (!contenidos.isEmpty()) {
            contenidoRepository.saveAll(contenidos);
        }

        List<RecursoContenido> recursos = recursoContenidoRepository.findByUnidadId(unidad.getId());
        recursos.forEach(recurso -> recurso.setEstado(unidad.getEstado()));
        if (!recursos.isEmpty()) {
            recursoContenidoRepository.saveAll(recursos);
        }
    }

    @Transactional
    public Unidad enviarARevision(Long id, Usuario currentUser) {
        Unidad unidad = getUnidad(id);
        authoringPolicy.assertCanSubmitReview(currentUser, unidad);
        unidad.setEstado(EstadoContenido.EN_REVISION);
        return unidadRepository.save(unidad);
    }

    @Transactional
    public void delete(Long id) {
        Unidad unidad = getUnidad(id);
        if (objetivoAprendizajeRepository.existsByUnidadId(id)
                || actividadRepository.existsByUnidadId(id)
                || asignacionRepository.existsByUnidadId(id)
                || informePedagogicoRepository.existsByUnidadId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una unidad que tiene objetivos, actividades, asignaciones o informes asociados"
            );
        }

        unidadRepository.delete(unidad);
    }

    private boolean isEmptyUpdate(Unidad cambios, Long autorizadaPorId) {
        return cambios.getNivelEducativo() == null
                && cambios.getTitulo() == null
                && cambios.getOrden() == null
                && cambios.getEstado() == null
                && autorizadaPorId == null
                && cambios.getFechaAutorizacion() == null;
    }

    private void validateAuthorization(Unidad unidad) {
        if (requiresAuthorization(unidad.getEstado())
                && (unidad.getAutorizadaPor() == null || unidad.getFechaAutorizacion() == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado AUTORIZADO o PUBLICADO requiere un usuario UTP y fecha de autorización"
            );
        }
    }

    private boolean requiresAuthorization(EstadoContenido estado) {
        return estado == EstadoContenido.AUTORIZADO || estado == EstadoContenido.PUBLICADO;
    }

    private Usuario getUsuarioUtp(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario UTP no encontrado"));
        if (usuario.getRol() != Rol.UTP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario autorizante debe tener rol UTP");
        }
        return usuario;
    }

    private Asignatura getAsignatura(Long id) {
        return asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignatura no encontrada"));
    }

    private Unidad getUnidad(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));
    }
}
