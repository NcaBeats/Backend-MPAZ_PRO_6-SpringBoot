package com.example.mpaz_pro_6springboot.informe.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;
import com.example.mpaz_pro_6springboot.common.mapper.ResumenMapper;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeContenido;
import com.example.mpaz_pro_6springboot.curriculo.model.PreguntaActividad;
import com.example.mpaz_pro_6springboot.curriculo.repository.ObjetivoAprendizajeContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.repository.PreguntaActividadRepository;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.model.Respuesta;
import com.example.mpaz_pro_6springboot.evaluacion.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.evaluacion.repository.RespuestaRepository;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.informe.dto.request.GenerarInformeRequest;
import com.example.mpaz_pro_6springboot.informe.dto.response.ActividadEvaluadaResumen;
import com.example.mpaz_pro_6springboot.informe.dto.response.DetallePreliminarResponse;
import com.example.mpaz_pro_6springboot.informe.dto.response.InformePedagogicoDetalladoResponse;
import com.example.mpaz_pro_6springboot.informe.dto.response.InformePreliminarResponse;
import com.example.mpaz_pro_6springboot.informe.mapper.DetalleInformePedagogicoMapper;
import com.example.mpaz_pro_6springboot.informe.mapper.InformePedagogicoMapper;
import com.example.mpaz_pro_6springboot.informe.model.DetalleInformePedagogico;
import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.model.OrigenInforme;
import com.example.mpaz_pro_6springboot.informe.repository.DetalleInformePedagogicoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Calcula el consolidado preliminar del desempeno de un estudiante en una
 * unidad y materializa ese consolidado como informe pedagogico.
 *
 * <p>El calculo usa el ultimo intento cerrado de cada actividad de la unidad,
 * con {@code fecha_fin <= fechaCorte}. La creacion guarda los conteos en
 * {@code detalle_informe_pedagogico}; a partir de ahi el informe es un
 * snapshot y no se recalcula.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InformePreliminarService {

    private final InformePedagogicoService informePedagogicoService;
    private final DetalleInformePedagogicoRepository detalleInformePedagogicoRepository;
    private final IntentoRepository intentoRepository;
    private final RespuestaRepository respuestaRepository;
    private final PreguntaActividadRepository preguntaActividadRepository;
    private final ObjetivoAprendizajeContenidoRepository objetivoAprendizajeContenidoRepository;
    private final ResumenMapper resumenMapper;
    private final InformePedagogicoMapper informePedagogicoMapper;
    private final DetalleInformePedagogicoMapper detalleInformePedagogicoMapper;

    /**
     * Devuelve el consolidado sin persistirlo. El {@code fechaCorte} de la
     * respuesta debe reenviarse al crear el informe para que el snapshot sea
     * consistente aunque el estudiante cierre nuevos intentos entre ambas
     * llamadas.
     */
    public InformePreliminarResponse previsualizar(
            Long usuarioId, Long unidadId, Instant fechaCorte) {
        Instant corte = normalizarFechaCorte(fechaCorte);
        return aRespuesta(calcular(usuarioId, unidadId, corte));
    }

    /**
     * Recalcula el consolidado con {@code fechaCorte} y persiste la cabecera y
     * los detalles en una sola transaccion.
     */
    @Transactional
    public InformePedagogicoDetalladoResponse generarDesdeResultados(
            GenerarInformeRequest request) {
        Instant corte = normalizarFechaCorte(request.fechaCorte());
        ResultadoCalculo resultado = calcular(request.usuarioId(), request.unidadId(), corte);

        if (resultado.detalles().isEmpty()) {
            throw new ReglaNegocioException(
                    "No hay objetivos de aprendizaje con resultados para la unidad y el corte indicados");
        }

        InformePedagogico informe = informePedagogicoService.crear(InformePedagogico.builder()
                .usuario(resultado.usuario())
                .unidad(resultado.unidad())
                .fecha(request.fecha())
                .descripcion(request.descripcion())
                .fechaCorte(corte)
                .origen(OrigenInforme.AUTOMATICO)
                .build());

        List<DetalleInformePedagogico> detalles = resultado.detalles().stream()
                .map(detalle -> DetalleInformePedagogico.builder()
                        .informePedagogico(informe)
                        .objetivoAprendizaje(detalle.objetivoAprendizaje())
                        .correctas(detalle.correctas())
                        .incorrectas(detalle.incorrectas())
                        .build())
                .toList();
        detalleInformePedagogicoRepository.saveAll(detalles);

        return new InformePedagogicoDetalladoResponse(
                informePedagogicoMapper.toResponse(informe),
                detalleInformePedagogicoMapper.toResponseList(detalles));
    }

    private ResultadoCalculo calcular(Long usuarioId, Long unidadId, Instant fechaCorte) {
        Usuario usuario = informePedagogicoService.resolverEstudiante(usuarioId);
        Unidad unidad = informePedagogicoService.resolverUnidad(unidadId);

        List<Long> intentoIds = intentoRepository.findIdsUltimosIntentosPorUnidad(
                usuarioId, unidadId, fechaCorte);
        if (intentoIds.isEmpty()) {
            return new ResultadoCalculo(usuario, unidad, fechaCorte, List.of(), List.of());
        }

        List<Intento> intentos = intentoRepository.findAllConActividadByIdIn(intentoIds);
        List<Long> actividadIds = intentos.stream()
                .map(intento -> intento.getActividadCurso().getActividad().getId())
                .distinct()
                .toList();

        Map<Long, List<PreguntaActividad>> preguntasPorActividad = preguntaActividadRepository
                .findAllConPreguntaByActividadIdIn(actividadIds).stream()
                .collect(Collectors.groupingBy(
                        preguntaActividad -> preguntaActividad.getActividad().getId()));

        Map<Long, Map<Long, List<Respuesta>>> respuestasPorIntento = respuestaRepository
                .findAllConDetalleByIntentoIdIn(intentoIds).stream()
                .collect(Collectors.groupingBy(
                        respuesta -> respuesta.getIntento().getId(),
                        Collectors.groupingBy(respuesta -> respuesta.getPregunta().getId())));

        Map<Long, List<ObjetivoAprendizaje>> objetivosPorContenido =
                objetivoAprendizajeContenidoRepository.findAllConObjetivoByUnidadId(unidadId)
                        .stream()
                        .collect(Collectors.groupingBy(
                                relacion -> relacion.getContenido().getId(),
                                Collectors.mapping(
                                        ObjetivoAprendizajeContenido::getObjetivoAprendizaje,
                                        Collectors.toList())));

        Map<Long, Acumulado> acumulados = new LinkedHashMap<>();
        for (Intento intento : intentos) {
            Long actividadId = intento.getActividadCurso().getActividad().getId();
            Map<Long, List<Respuesta>> respuestasDelIntento = respuestasPorIntento
                    .getOrDefault(intento.getId(), Map.of());

            for (PreguntaActividad preguntaActividad : preguntasPorActividad
                    .getOrDefault(actividadId, List.of())) {
                Long preguntaId = preguntaActividad.getPregunta().getId();
                List<Respuesta> seleccionadas = respuestasDelIntento
                        .getOrDefault(preguntaId, List.of());
                if (seleccionadas.isEmpty()) {
                    continue;
                }

                // Regla estricta: la pregunta es correcta solo si todas las
                // alternativas seleccionadas son correctas.
                boolean correcta = seleccionadas.stream()
                        .allMatch(respuesta -> respuesta.getAlternativa().isCorrecta());
                Long contenidoId = preguntaActividad.getPregunta().getRecurso()
                        .getContenido().getId();

                for (ObjetivoAprendizaje objetivo : objetivosPorContenido
                        .getOrDefault(contenidoId, List.of())) {
                    Acumulado acumulado = acumulados.computeIfAbsent(
                            objetivo.getId(), id -> new Acumulado(objetivo));
                    if (correcta) {
                        acumulado.correctas++;
                    } else {
                        acumulado.incorrectas++;
                    }
                }
            }
        }

        List<DetalleCalculado> detalles = acumulados.values().stream()
                .map(acumulado -> new DetalleCalculado(
                        acumulado.objetivoAprendizaje,
                        acumulado.correctas,
                        acumulado.incorrectas))
                .sorted(Comparator.comparing(detalle -> detalle.objetivoAprendizaje().getCodigo()))
                .toList();

        return new ResultadoCalculo(usuario, unidad, fechaCorte, intentos, detalles);
    }

    private InformePreliminarResponse aRespuesta(ResultadoCalculo resultado) {
        List<DetallePreliminarResponse> detalles = resultado.detalles().stream()
                .map(detalle -> new DetallePreliminarResponse(
                        resumenMapper.toResumen(detalle.objetivoAprendizaje()),
                        detalle.correctas(),
                        detalle.incorrectas(),
                        detalle.total(),
                        detalle.porcentaje()))
                .toList();

        int totalCorrectas = detalles.stream()
                .mapToInt(DetallePreliminarResponse::correctas)
                .sum();
        int totalIncorrectas = detalles.stream()
                .mapToInt(DetallePreliminarResponse::incorrectas)
                .sum();

        List<ActividadEvaluadaResumen> actividades = resultado.intentos().stream()
                .map(this::aActividadResumen)
                .toList();

        return new InformePreliminarResponse(
                resumenMapper.toResumen(resultado.usuario()),
                resumenMapper.toResumen(resultado.unidad()),
                resultado.fechaCorte(),
                actividades,
                detalles,
                totalCorrectas,
                totalIncorrectas);
    }

    private ActividadEvaluadaResumen aActividadResumen(Intento intento) {
        return new ActividadEvaluadaResumen(
                intento.getActividadCurso().getId(),
                intento.getActividadCurso().getActividad().getId(),
                intento.getActividadCurso().getActividad().getInstrucciones(),
                intento.getId(),
                intento.getFechaFin());
    }

    private Instant normalizarFechaCorte(Instant fechaCorte) {
        Instant ahora = Instant.now();
        Instant corte = fechaCorte != null ? fechaCorte : ahora;
        if (corte.isAfter(ahora)) {
            throw new ReglaNegocioException("La fecha de corte no puede ser futura");
        }
        return corte;
    }

    private record ResultadoCalculo(
            Usuario usuario,
            Unidad unidad,
            Instant fechaCorte,
            List<Intento> intentos,
            List<DetalleCalculado> detalles) {
    }

    private record DetalleCalculado(
            ObjetivoAprendizaje objetivoAprendizaje,
            int correctas,
            int incorrectas) {

        int total() {
            return correctas + incorrectas;
        }

        double porcentaje() {
            int total = total();
            if (total == 0) {
                return 0.0;
            }
            double valor = (correctas * 100.0) / total;
            return Math.round(valor * 100.0) / 100.0;
        }
    }

    private static final class Acumulado {

        private final ObjetivoAprendizaje objetivoAprendizaje;
        private int correctas;
        private int incorrectas;

        private Acumulado(ObjetivoAprendizaje objetivoAprendizaje) {
            this.objetivoAprendizaje = objetivoAprendizaje;
        }
    }
}
