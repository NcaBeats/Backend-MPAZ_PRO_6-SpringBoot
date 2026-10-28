package com.example.mpaz_pro_6springboot.evaluacion.intento.service;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.common.security.EvaluacionAccessPolicy;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntentoService {

    private final IntentoRepository intentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;
    private final AsignacionRepository asignacionRepository;
    private final AsignacionActividadRepository asignacionActividadRepository;
    private final EvaluacionAccessPolicy evaluacionAccessPolicy;

    public List<Intento> findAll(Long estudianteId, Long actividadId, Usuario currentUser) {
        Long efectivoEstudianteId = evaluacionAccessPolicy.scopeEstudianteId(estudianteId, currentUser);
        if (efectivoEstudianteId != null) {
            getUsuario(efectivoEstudianteId);
        }
        if (actividadId != null) {
            getActividad(actividadId);
        }

        if (efectivoEstudianteId != null && actividadId != null) {
            return intentoRepository.findByEstudianteIdAndActividadIdOrderByNumeroAsc(efectivoEstudianteId, actividadId);
        }
        if (efectivoEstudianteId != null) {
            return intentoRepository.findByEstudianteIdOrderByActividadIdAscNumeroAsc(efectivoEstudianteId);
        }
        if (actividadId != null) {
            return intentoRepository.findByActividadIdOrderByEstudianteIdAscNumeroAsc(actividadId);
        }
        return intentoRepository.findAllByOrderByEstudianteIdAscActividadIdAscNumeroAsc();
    }

    public Intento findById(Long id, Usuario currentUser) {
        Intento intento = getIntento(id);
        evaluacionAccessPolicy.assertPuedeLeer(intento, currentUser);
        return intento;
    }

    @Transactional
    public Intento create(Long estudianteId, Long actividadId, Usuario currentUser) {
        evaluacionAccessPolicy.assertPuedeIniciar(estudianteId, currentUser);
        Usuario estudiante = getUsuario(estudianteId);
        if (estudiante.getRol() != Rol.ESTUDIANTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario que inicia un intento debe tener rol ESTUDIANTE"
            );
        }
        if (estudiante.getCurso() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estudiante no tiene un curso asignado"
            );
        }

        Actividad actividad = getActividad(actividadId);
        Unidad unidad = actividad.getUnidad();
        if (!esUnidadVisible(unidad.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La unidad de la actividad debe estar AUTORIZADA o PUBLICADA"
            );
        }

        Asignacion asignacion = asignacionRepository
                .findByCursoIdAndUnidadId(estudiante.getCurso().getId(), unidad.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La unidad de la actividad no está asignada al curso del estudiante"
                ));
        if (!asignacionActividadRepository.existsByAsignacionIdAndActividadId(asignacion.getId(), actividadId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La actividad no está asignada al curso del estudiante"
            );
        }

        if (actividad.getTipo() == TipoActividad.PRUEBA_FINAL) {
            if (!Boolean.TRUE.equals(asignacion.getPruebaFinalHabilitada())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La prueba final no está habilitada para la unidad"
                );
            }
            Integer maxIntentos = asignacion.getMaxIntentos();
            if (maxIntentos != null) {
                long usados = intentoRepository.countByEstudianteIdAndActividadId(estudianteId, actividadId);
                if (usados >= maxIntentos) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Se alcanzó el límite de intentos de la prueba final"
                    );
                }
            }
        }

        int numero = intentoRepository.findMaxNumero(estudianteId, actividadId) + 1;
        Intento intento = Intento.builder()
                .estudiante(estudiante)
                .actividad(actividad)
                .numero(numero)
                .fechaInicio(Instant.now())
                .build();
        return intentoRepository.save(intento);
    }

    @Transactional
    public Intento completar(Long id, Usuario currentUser) {
        Intento intento = getIntento(id);
        evaluacionAccessPolicy.assertPuedeOperar(intento, currentUser);
        if (intento.getFechaFin() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El intento ya está completado"
            );
        }
        intento.setFechaFin(Instant.now());
        return intentoRepository.save(intento);
    }

    private boolean esUnidadVisible(EstadoContenido estado) {
        return estado == EstadoContenido.AUTORIZADO || estado == EstadoContenido.PUBLICADO;
    }

    private Intento getIntento(Long id) {
        return intentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intento no encontrado"));
    }

    private Usuario getUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Actividad getActividad(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }
}
