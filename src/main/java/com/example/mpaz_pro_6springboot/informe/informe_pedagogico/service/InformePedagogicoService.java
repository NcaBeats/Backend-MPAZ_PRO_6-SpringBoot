package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.service;

import com.example.mpaz_pro_6springboot.common.enums.EstadoInforme;
import com.example.mpaz_pro_6springboot.common.enums.NivelOa;
import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.common.security.InformeAccessPolicy;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.docencia.model.DocenteCursoAsignatura;
import com.example.mpaz_pro_6springboot.docencia.docencia.repository.DocenteCursoAsignaturaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.model.RespuestaEstudiante;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.repository.RespuestaEstudianteRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model.DetalleInformeOa;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository.InformePedagogicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InformePedagogicoService {

    private static final double UMBRAL_FORTALEZA = 70.0;

    private final InformePedagogicoRepository informeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadRepository unidadRepository;
    private final ActividadRepository actividadRepository;
    private final IntentoRepository intentoRepository;
    private final RespuestaEstudianteRepository respuestaEstudianteRepository;
    private final AsignacionRepository asignacionRepository;
    private final AsignacionActividadRepository asignacionActividadRepository;
    private final DocenteCursoAsignaturaRepository docenciaRepository;
    private final InformeAccessPolicy informeAccessPolicy;

    public List<InformePedagogico> findAll(Long estudianteId, Long unidadId, Usuario currentUser) {
        informeAccessPolicy.assertCanList(currentUser);
        if (estudianteId != null) {
            getUsuario(estudianteId);
        }
        if (unidadId != null) {
            getUnidad(unidadId);
        }

        Long docenteId = currentUser.getId();
        if (estudianteId != null && unidadId != null) {
            return informeRepository
                    .findByDocenteIdAndEstudianteIdAndUnidadIdOrderByIdAsc(
                            docenteId,
                            estudianteId,
                            unidadId
                    );
        }
        if (estudianteId != null) {
            return informeRepository.findByDocenteIdAndEstudianteIdOrderByIdAsc(
                    docenteId,
                    estudianteId
            );
        }
        if (unidadId != null) {
            return informeRepository.findByDocenteIdAndUnidadIdOrderByIdAsc(
                    docenteId,
                    unidadId
            );
        }
        return informeRepository.findByDocenteIdOrderByIdAsc(docenteId);
    }

    public InformePedagogico findById(Long id, Usuario currentUser) {
        InformePedagogico informe = getInforme(id);
        informeAccessPolicy.assertCanRead(informe, currentUser);
        return informe;
    }

    @Transactional
    public InformePedagogico generar(Long estudianteId, Long unidadId, Long docenteId, Usuario currentUser) {
        informeAccessPolicy.assertCanGenerate(docenteId, currentUser);
        Usuario estudiante = getUsuario(estudianteId);
        if (estudiante.getRol() != Rol.ESTUDIANTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estudiante del informe debe tener rol ESTUDIANTE"
            );
        }
        Usuario docente = getUsuario(docenteId);
        if (docente.getRol() != Rol.DOCENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El docente del informe debe tener rol DOCENTE"
            );
        }
        Unidad unidad = getUnidad(unidadId);

        if (estudiante.getCurso() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estudiante no tiene un curso asignado"
            );
        }

        DocenteCursoAsignatura docencia = docenciaRepository
                .findByCursoIdAndAsignaturaId(
                        estudiante.getCurso().getId(),
                        unidad.getAsignatura().getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No tienes docencia para la asignatura del estudiante"
                ));
        if (!docencia.getDocente().getId().equals(docenteId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el docente de la asignatura puede generar el informe"
            );
        }

        if (informeRepository.existsByEstudianteIdAndUnidadId(estudianteId, unidadId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un informe del estudiante para la unidad"
            );
        }

        Asignacion asignacion = asignacionRepository
                .findByCursoIdAndUnidadId(estudiante.getCurso().getId(), unidadId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El estudiante no tiene una asignación para esa unidad"
                ));
        Set<Long> actividadesAsignadas = asignacionActividadRepository
                .findByAsignacionId(asignacion.getId())
                .stream()
                .map(asignacionActividad -> asignacionActividad.getActividad().getId())
                .collect(Collectors.toSet());
        if (actividadesAsignadas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La asignación no tiene actividades para generar el informe"
            );
        }

        Map<Long, int[]> acumulado = new LinkedHashMap<>();
        Map<Long, ObjetivoAprendizaje> oas = new LinkedHashMap<>();

        for (Actividad actividad : actividadRepository.findByUnidadId(unidadId)) {
            if (!actividadesAsignadas.contains(actividad.getId())
                    || actividad.getTipo() == TipoActividad.PRUEBA_FINAL) {
                continue;
            }
            Optional<Intento> ultimo = intentoRepository
                    .findFirstByEstudianteIdAndActividadIdAndFechaFinIsNotNullOrderByNumeroDesc(estudianteId, actividad.getId());
            if (ultimo.isEmpty()) {
                continue;
            }
            for (RespuestaEstudiante respuesta : respuestaEstudianteRepository
                    .findByIntentoIdOrderByIdAsc(ultimo.get().getId())) {
                ObjetivoAprendizaje oa = respuesta.getPregunta().getOa();
                int[] contadores = acumulado.computeIfAbsent(oa.getId(), k -> new int[2]);
                contadores[0] = contadores[0] + 1;
                if (Boolean.TRUE.equals(respuesta.getEsCorrecta())) {
                    contadores[1] = contadores[1] + 1;
                }
                oas.put(oa.getId(), oa);
            }
        }

        if (acumulado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No hay respuestas de actividades asignadas para generar el informe"
            );
        }

        InformePedagogico informe = InformePedagogico.builder()
                .estudiante(estudiante)
                .unidad(unidad)
                .docente(docente)
                .fechaGeneracion(Instant.now())
                .estado(EstadoInforme.PENDIENTE_VALIDACION)
                .build();

        acumulado.forEach((oaId, contadores) -> {
            int totalPreguntas = contadores[0];
            int correctas = contadores[1];
            informe.getDetallesOa().add(DetalleInformeOa.builder()
                    .informe(informe)
                    .oa(oas.get(oaId))
                    .totalPreguntas(totalPreguntas)
                    .correctas(correctas)
                    .nivel(nivelDe(correctas, totalPreguntas))
                    .build());
        });

        InformePedagogico guardado = informeRepository.save(informe);
        // Se relee con EntityGraph para que el mapper (fuera de la transaccion)
        // pueda acceder a unidad.asignatura y a los nombres sin LazyInitializationException.
        return getInforme(guardado.getId());
    }

    @Transactional
    public InformePedagogico validar(Long id, Usuario currentUser) {
        InformePedagogico informe = getInforme(id);
        informeAccessPolicy.assertCanValidate(informe, currentUser);
        if (informe.getEstado() == EstadoInforme.VALIDADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El informe ya está validado"
            );
        }
        informe.setEstado(EstadoInforme.VALIDADO);
        informe.setFechaValidacion(Instant.now());
        return informeRepository.save(informe);
    }

    private NivelOa nivelDe(int correctas, int totalPreguntas) {
        double porcentaje = totalPreguntas == 0 ? 0.0 : correctas * 100.0 / totalPreguntas;
        return porcentaje >= UMBRAL_FORTALEZA ? NivelOa.FORTALEZA : NivelOa.REFUERZO;
    }

    private InformePedagogico getInforme(Long id) {
        return informeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Informe no encontrado"));
    }

    private Usuario getUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Unidad getUnidad(Long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));
    }
}
