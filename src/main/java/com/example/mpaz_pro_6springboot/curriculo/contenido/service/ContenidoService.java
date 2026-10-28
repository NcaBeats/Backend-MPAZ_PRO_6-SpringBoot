package com.example.mpaz_pro_6springboot.curriculo.contenido.service;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import com.example.mpaz_pro_6springboot.common.security.CurriculoAuthoringPolicy;
import com.example.mpaz_pro_6springboot.common.security.StudentVisibilityService;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContenidoService {

    private static final int ORDEN_PREDETERMINADO = 1;

    private final ContenidoRepository contenidoRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final ActividadRepository actividadRepository;
    private final CurriculoAuthoringPolicy authoringPolicy;
    private final StudentVisibilityService visibilityService;

    public List<Contenido> findAll(Long oaId, Usuario currentUser) {
        if (oaId != null) {
            getObjetivoAprendizaje(oaId);
            if (!visibilityService.canSeeObjective(currentUser, oaId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El objetivo no está asociado a una asignación del estudiante"
                );
            }
        }

        if (!visibilityService.isStudent(currentUser)) {
            return oaId == null
                    ? contenidoRepository.findAllByOrderByOaIdAscOrdenAscIdAsc()
                    : contenidoRepository.findByOaIdOrderByOrdenAscIdAsc(oaId);
        }

        var visibles = visibilityService.visibleObjectiveIds(currentUser);
        return contenidoRepository.findAllByOrderByOaIdAscOrdenAscIdAsc()
                .stream()
                .filter(contenido -> contenido.getEstado() == EstadoContenido.PUBLICADO)
                .filter(contenido -> visibles.contains(contenido.getOa().getId()))
                .filter(contenido -> oaId == null || oaId.equals(contenido.getOa().getId()))
                .toList();
    }

    public Contenido findById(Long id, Usuario currentUser) {
        Contenido contenido = getContenido(id);
        if (visibilityService.isStudent(currentUser)
                && contenido.getEstado() != EstadoContenido.PUBLICADO) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Contenido no encontrado"
            );
        }
        if (!visibilityService.canSeeObjective(currentUser, contenido.getOa().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El contenido no está asociado a una asignación del estudiante"
            );
        }
        return contenido;
    }

    @Transactional
    public Contenido create(Contenido contenido, Long oaId, Usuario currentUser) {
        ObjetivoAprendizaje objetivo = getObjetivoAprendizaje(oaId);
        authoringPolicy.assertCanAuthor(currentUser, objetivo.getUnidad());

        contenido.setOa(objetivo);
        // El estado inicial del material es el de su unidad: un docente solo
        // puede crear mientras la unidad está en BORRADOR; la UTP conserva el
        // acceso administrativo.
        contenido.setEstado(objetivo.getUnidad().getEstado());
        if (contenido.getOrden() == null) {
            contenido.setOrden(ORDEN_PREDETERMINADO);
        }
        return contenidoRepository.save(contenido);
    }

    @Transactional
    public Contenido update(Long id, Contenido cambios) {
        if (isEmptyUpdate(cambios)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar al menos un campo para actualizar"
            );
        }

        Contenido contenido = getContenido(id);
        if (cambios.getTitulo() != null) {
            contenido.setTitulo(cambios.getTitulo());
        }
        if (cambios.getExplicacion() != null) {
            contenido.setExplicacion(cambios.getExplicacion());
        }
        if (cambios.getEjemplos() != null) {
            contenido.setEjemplos(cambios.getEjemplos());
        }
        if (cambios.getInstrucciones() != null) {
            contenido.setInstrucciones(cambios.getInstrucciones());
        }
        if (cambios.getOrden() != null) {
            contenido.setOrden(cambios.getOrden());
        }
        if (cambios.getOrigen() != null) {
            contenido.setOrigen(cambios.getOrigen());
        }
        return contenidoRepository.save(contenido);
    }

    @Transactional
    public void delete(Long id) {
        Contenido contenido = getContenido(id);
        if (actividadRepository.existsByContenidoId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un contenido con actividades asociadas"
            );
        }
        contenidoRepository.delete(contenido);
    }

    private boolean isEmptyUpdate(Contenido cambios) {
        return cambios.getTitulo() == null
                && cambios.getExplicacion() == null
                && cambios.getEjemplos() == null
                && cambios.getInstrucciones() == null
                && cambios.getOrden() == null
                && cambios.getOrigen() == null;
    }

    private Contenido getContenido(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido no encontrado"));
    }

    private ObjetivoAprendizaje getObjetivoAprendizaje(Long id) {
        return objetivoAprendizajeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Objetivo de aprendizaje no encontrado"
                ));
    }
}
