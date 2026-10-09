package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.service;

import com.example.mpaz_pro_6springboot.common.enums.EstadoInforme;
import com.example.mpaz_pro_6springboot.common.enums.NivelOa;
import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
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

    public List<InformePedagogico> findAll(Long estudianteId, Long unidadId) {
        if (estudianteId != null) {
            getUsuario(estudianteId);
        }
        if (unidadId != null) {
            getUnidad(unidadId);
        }

        if (estudianteId != null && unidadId != null) {
            return informeRepository.findByEstudianteIdAndUnidadIdOrderByIdAsc(estudianteId, unidadId);
        }
        if (estudianteId != null) {
            return informeRepository.findByEstudianteIdOrderByIdAsc(estudianteId);
        }
        if (unidadId != null) {
            return informeRepository.findByUnidadIdOrderByIdAsc(unidadId);
        }
        return informeRepository.findAllByOrderByIdAsc();
    }

    public InformePedagogico findById(Long id) {
        return getInforme(id);
    }

    @Transactional
    public InformePedagogico generar(Long estudianteId, Long unidadId, Long docenteId) {
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

        if (informeRepository.existsByEstudianteIdAndUnidadId(estudianteId, unidadId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un informe del estudiante para la unidad"
            );
        }

        Map<Long, int[]> acumulado = new LinkedHashMap<>();
        Map<Long, ObjetivoAprendizaje> oas = new LinkedHashMap<>();

        for (Actividad actividad : actividadRepository.findByUnidadId(unidadId)) {
            Optional<Intento> ultimo = intentoRepository
                    .findUltimoCompletadoByEstudianteAndActividad(estudianteId, actividad.getId());
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
                    "No hay respuestas de intentos completados para generar el informe"
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

        return informeRepository.save(informe);
    }

    @Transactional
    public InformePedagogico validar(Long id) {
        InformePedagogico informe = getInforme(id);
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
