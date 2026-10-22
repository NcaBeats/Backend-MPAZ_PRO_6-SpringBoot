package com.example.mpaz_pro_6springboot.common.seed;

import com.example.mpaz_pro_6springboot.common.enums.Dificultad;
import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import com.example.mpaz_pro_6springboot.common.enums.EstadoInforme;
import com.example.mpaz_pro_6springboot.common.enums.NivelOa;
import com.example.mpaz_pro_6springboot.common.enums.OrigenMaterial;
import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import com.example.mpaz_pro_6springboot.common.enums.TipoRecurso;
import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.actividad.repository.ActividadRepository;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.alternativa.repository.AlternativaRepository;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.asignatura.repository.AsignaturaRepository;
import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.contenido.repository.ContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository.ObjetivoAprendizajeRepository;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.pregunta.repository.PreguntaRepository;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.repository.RecursoContenidoRepository;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.curriculo.unidad.repository.UnidadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.docencia.asignacion.repository.AsignacionRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividad;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividadId;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository.AsignacionActividadRepository;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOa;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOaId;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.repository.AsignacionOaRepository;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.evaluacion.intento.repository.IntentoRepository;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.model.RespuestaEstudiante;
import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.repository.RespuestaEstudianteRepository;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.curso.repository.CursoRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.identidad.usuario.repository.UsuarioRepository;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model.DetalleInformeOa;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.repository.DetalleInformeOaRepository;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository.InformePedagogicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga los datos de demo (especificaciones/mpaz_pro_modelo_definitivo.md,
 * seccion 11) la primera vez que arranca la aplicacion, para que el proyecto
 * quede listo para consultar sin pasos manuales.
 *
 * - Idempotente: si ya hay usuarios, no hace nada.
 * - Se puede desactivar con mpaz.seed.enabled=false.
 * - Inserta con repositorios (no con Services) porque el seed representa un
 *   estado ya consolidado: incluye intentos completados y un snapshot de
 *   informe que no debe recalcularse al arrancar.
 * - Las contrasenas son ficticias: utp/utp12345, docente/doc12345 y
 *   estudiantes/alu12345. Se guardan con BCrypt real.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "mpaz.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements ApplicationRunner {

    private static final String PASSWORD_UTP = "utp12345";
    private static final String PASSWORD_DOCENTE = "doc12345";
    private static final String PASSWORD_ESTUDIANTE = "alu12345";

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final UnidadRepository unidadRepository;
    private final ObjetivoAprendizajeRepository objetivoAprendizajeRepository;
    private final ContenidoRepository contenidoRepository;
    private final RecursoContenidoRepository recursoContenidoRepository;
    private final ActividadRepository actividadRepository;
    private final PreguntaRepository preguntaRepository;
    private final AlternativaRepository alternativaRepository;
    private final AsignacionRepository asignacionRepository;
    private final AsignacionOaRepository asignacionOaRepository;
    private final AsignacionActividadRepository asignacionActividadRepository;
    private final IntentoRepository intentoRepository;
    private final RespuestaEstudianteRepository respuestaEstudianteRepository;
    private final InformePedagogicoRepository informePedagogicoRepository;
    private final DetalleInformeOaRepository detalleInformeOaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            log.info("DataInitializer: la base ya tiene datos, no se vuelve a cargar el seed");
            return;
        }

        seed();

        log.info("DataInitializer: datos de demo cargados (4 unidades, 12 actividades, 4 intentos, 1 informe)");
    }

    private void seed() {
        Curso curso = cursoRepository.save(Curso.builder()
                .nombre("6A")
                .anio(2026)
                .build());

        Usuarios usuarios = crearUsuarios(curso);
        List<Asignatura> asignaturas = crearAsignaturas();
        List<Unidad> unidades = crearUnidades(asignaturas, usuarios.utp());
        List<ObjetivoAprendizaje> oas = crearObjetivos(unidades);
        List<Contenido> contenidos = crearContenidos(oas);
        crearRecursos(contenidos);

        List<Actividad> actividades = crearActividades(unidades);
        Actividad pruebaFinalUnidad1 = actividades.get(2);
        BancoPreguntas banco = crearPreguntas(unidades.get(0), pruebaFinalUnidad1, oas.get(0), oas.get(1));

        List<Asignacion> asignaciones = crearAsignaciones(curso, unidades, usuarios.docente());
        crearAsignacionesOa(asignaciones, oas);
        crearAsignacionesActividad(asignaciones, actividades);

        Intentos intentos = crearIntentos(usuarios, pruebaFinalUnidad1);
        crearRespuestas(intentos, banco);

        crearInforme(usuarios, unidades.get(0), oas);
    }

    // ------------------------------------------------------------------
    // A) Curso y usuarios
    // ------------------------------------------------------------------

    private Usuarios crearUsuarios(Curso curso) {
        String hashUtp = passwordEncoder.encode(PASSWORD_UTP);
        String hashDocente = passwordEncoder.encode(PASSWORD_DOCENTE);
        String hashEstudiante = passwordEncoder.encode(PASSWORD_ESTUDIANTE);

        Usuario utp = usuarioRepository.save(Usuario.builder()
                .nombre("Patricia Anilin")
                .username("utp")
                .passwordHash(hashUtp)
                .rol(Rol.UTP)
                .build());

        Usuario docente = usuarioRepository.save(Usuario.builder()
                .nombre("Diego Salinas")
                .username("docente")
                .passwordHash(hashDocente)
                .rol(Rol.DOCENTE)
                .build());

        Usuario camila = usuarioRepository.save(estudiante("Camila Rivas", "camila", hashEstudiante, curso));
        Usuario mateo = usuarioRepository.save(estudiante("Mateo Cortes", "mateo", hashEstudiante, curso));
        Usuario javiera = usuarioRepository.save(estudiante("Javiera Munoz", "javiera", hashEstudiante, curso));
        Usuario benjamin = usuarioRepository.save(estudiante("Benjamin Ortiz", "benjamin", hashEstudiante, curso));

        return new Usuarios(utp, docente, camila, mateo, javiera, benjamin);
    }

    private Usuario estudiante(String nombre, String username, String hash, Curso curso) {
        return Usuario.builder()
                .nombre(nombre)
                .username(username)
                .passwordHash(hash)
                .rol(Rol.ESTUDIANTE)
                .curso(curso)
                .build();
    }

    // ------------------------------------------------------------------
    // B) Asignaturas y unidades
    // ------------------------------------------------------------------

    private List<Asignatura> crearAsignaturas() {
        return List.of(
                asignaturaRepository.save(Asignatura.builder().nombre("Lengua y Comunicacion").build()),
                asignaturaRepository.save(Asignatura.builder().nombre("Matematica").build()),
                asignaturaRepository.save(Asignatura.builder().nombre("Ciencias Naturales").build()),
                asignaturaRepository.save(Asignatura.builder().nombre("Historia, Geografia y Ciencias Sociales").build())
        );
    }

    private List<Unidad> crearUnidades(List<Asignatura> asignaturas, Usuario utp) {
        return List.of(
                unidadRepository.save(Unidad.builder()
                        .asignatura(asignaturas.get(0))
                        .titulo("Lectura y comprehension de textos")
                        .orden(1)
                        .estado(EstadoContenido.AUTORIZADO)
                        .autorizadaPor(utp)
                        .fechaAutorizacion(Instant.parse("2026-03-02T09:00:00Z"))
                        .build()),
                unidadRepository.save(Unidad.builder()
                        .asignatura(asignaturas.get(1))
                        .titulo("Numeros, operaciones y problemas")
                        .orden(1)
                        .estado(EstadoContenido.AUTORIZADO)
                        .autorizadaPor(utp)
                        .fechaAutorizacion(Instant.parse("2026-03-02T09:05:00Z"))
                        .build()),
                unidadRepository.save(Unidad.builder()
                        .asignatura(asignaturas.get(2))
                        .titulo("Materia y energia en los seres vivos")
                        .orden(1)
                        .estado(EstadoContenido.PUBLICADO)
                        .autorizadaPor(utp)
                        .fechaAutorizacion(Instant.parse("2026-03-02T09:10:00Z"))
                        .build()),
                unidadRepository.save(Unidad.builder()
                        .asignatura(asignaturas.get(3))
                        .titulo("Las sociedades en el tiempo y el espacio")
                        .orden(1)
                        .estado(EstadoContenido.PUBLICADO)
                        .autorizadaPor(utp)
                        .fechaAutorizacion(Instant.parse("2026-03-02T09:15:00Z"))
                        .build())
        );
    }

    // ------------------------------------------------------------------
    // C) Objetivos de aprendizaje: 2 por unidad
    // ------------------------------------------------------------------

    private List<ObjetivoAprendizaje> crearObjetivos(List<Unidad> unidades) {
        List<ObjetivoAprendizaje> oas = new ArrayList<>();

        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(0), "OA-LC-6A-01",
                "Comprender el proposito y la idea principal de un texto informativo breve, distinguiendo tema y detalles.",
                "Lectura y comprension")));
        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(0), "OA-LC-6A-02",
                "Interpretar mensajes que combinan lenguaje verbal y no verbal, como titulos, subtitulos e imagenes de apoyo.",
                "Comprension multimodal")));
        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(1), "OA-MA-6A-01",
                "Resolver problemas de la vida diaria usando operaciones con numeros naturales y decimales.",
                "Numeros y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(1), "OA-MA-6A-02",
                "Reconocer y describir patrones numericos y geometricos presentes en objetos y figuras.",
                "Patrones y formas")));
        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(2), "OA-CN-6A-01",
                "Explicar como se organiza la materia en los seres vivos y que cambios sofre en un proceso observable.",
                "Materia y energia")));
        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(2), "OA-CN-6A-02",
                "Relacionar la luz con la produccion de energia en las plantas y su importancia para los otros seres vivos.",
                "Luz y vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(3), "OA-HG-6A-01",
                "Ubicar en el tiempo y en el espacio hechos y fenomenos sociales, usando categorias simples.",
                "Tiempo y espacio")));
        oas.add(objetivoAprendizajeRepository.save(oa(unidades.get(3), "OA-HG-6A-02",
                "Reconocer la influencia de las actividades humanas en el ambiente de su comunidad.",
                "Convivencia y ambiente")));

        return oas;
    }

    private ObjetivoAprendizaje oa(Unidad unidad, String codigo, String descripcion, String eje) {
        return ObjetivoAprendizaje.builder()
                .unidad(unidad)
                .codigo(codigo)
                .descripcion(descripcion)
                .eje(eje)
                .textoReferencia(null)
                .build();
    }

    // ------------------------------------------------------------------
    // D) Contenidos y recursos: 1 por OA
    // ------------------------------------------------------------------

    private List<Contenido> crearContenidos(List<ObjetivoAprendizaje> oas) {
        List<Contenido> contenidos = new ArrayList<>();

        contenidos.add(contenidoRepository.save(contenido(oas.get(0),
                "De que se trata un texto informativo",
                "Un texto informativo explica algo de forma ordenada. Su idea principal responde a la pregunta de que trata, y los detalles la acompanan sin reemplazarla. Para encontrarla, conviene leer el titulo y el primer parrafo antes de seguir.",
                "Un texto sobre el ciclo del agua suele decir en su primera linea que tratara sobre ese proceso.",
                "Busca el titulo y la primera frase. Escribi en una linea de que trata el texto.")));

        contenidos.add(contenidoRepository.save(contenido(oas.get(1),
                "Cuando la imagen completa al texto",
                "Muchos textos informativos incorporan imagenes, diagramas o infografias. La imagen no decora: aporta datos que el texto no repite. Para leerla hay que identificar que muestra, que rotulos tiene y que relacion propone.",
                "En un diagrama de etapas, cada recuadro es un paso y las flechas indican el orden.",
                "Mira la imagen y responde: que dato agrega al texto y donde lo encuentras.")));

        contenidos.add(contenidoRepository.save(contenido(oas.get(2),
                "Elegir la operacion que corresponde",
                "Un problema de la vida diaria se resuelve identificando primero que se pide y despues eligiendo la operacion. Sumar acumula, restar quita, multiplicar repite y divide repartir. Elegir mal la operacion es la causa mas comun de error.",
                "Si hay 6 cajas con 8 utiles cada una y se necesitan 40, toca multiplicar y luego restar.",
                "Subraya que se pide antes de calcular. Anota la operacion que elegiste y por que.")));

        contenidos.add(contenidoRepository.save(contenido(oas.get(3),
                "Patrones que se repiten",
                "Un patron es una secuencia que se repite con la misma regla. Se puede describir, continuar y anticipar. En figuras, el patron suele ser una regla de posicion o de forma que se aplica a cada elemento.",
                "Una fila de 3 cuadrados y un circulo que se repite cada cuatro figuras.",
                "Completa la secuencia y explica la regla con la que la completaste.")));

        contenidos.add(contenidoRepository.save(contenido(oas.get(4),
                "La materia cambia de forma",
                "La materia es todo lo que ocupa espacio. En los seres vivos cambia de forma sin dejar de ser materia: el hielo se derrite, una hoja verde se pone amarilla, la madera se convierte en ceniza. Estos cambios se pueden describir y observar.",
                "Al dejar un cubo de hielo a temperatura ambiente, pasa de solido a liquido sin volverse otra sustancia.",
                "Describe un cambio de estado o de aspecto que hayas observado en una planta o en un objeto.")));

        contenidos.add(contenidoRepository.save(contenido(oas.get(5),
                "De donde sale la energia de una planta",
                "Las plantas captan energia de la luz y la usan para fabricar su propio alimento. Ese alimento es la base de la cadena alimentaria: sin el, los animales no tendrian energia disponible.",
                "Por eso las areas con poca luz tienen menos vegetacion y menos animales asociados.",
                "Explica con tus palabras por que se dice que las plantas son la base de la cadena alimentaria.")));

        contenidos.add(contenidoRepository.save(contenido(oas.get(6),
                "Ubicar un hecho en el tiempo y el lugar",
                "Ubicar un hecho significa ubicar cuando ocurrio y donde. El tiempo se ordena con categorias amplias y el lugar se ubica con un punto de referencia mas general. Un mismo hecho puede explicarse distinto segun desde que lugar se mire.",
                "La migracion del chancho de azucar se asocia al invierno en el hemisferio sur.",
                "Ubica un hecho que ocurrio en tu comuna y di por que importa saber donde y cuando.")));

        contenidos.add(contenidoRepository.save(contenido(oas.get(7),
                "Como las personas cambian su entorno",
                "Las actividades humanas, como construir, cultivar o sacar minerales, modifican el ambiente. Los cambios pueden mejorar o deteriorar, y el efecto depende de como se haga la actividad.",
                "Un parque bien cuidado mantiene arboles y deja el suelo permeable; uno descuidado pierde vegetacion.",
                "Describe una actividad de tu barrio y que cambio produce en el ambiente.")));

        return contenidos;
    }

    private Contenido contenido(ObjetivoAprendizaje oa, String titulo, String explicacion,
                                String ejemplos, String instrucciones) {
        return Contenido.builder()
                .oa(oa)
                .titulo(titulo)
                .explicacion(explicacion)
                .ejemplos(ejemplos)
                .instrucciones(instrucciones)
                .orden(1)
                .origen(OrigenMaterial.PROPIO)
                .build();
    }

    private void crearRecursos(List<Contenido> contenidos) {
        recursoContenidoRepository.save(recurso(contenidos.get(0), TipoRecurso.IMAGEN,
                "recursos/texto-informativo-organizacion.png",
                "Diagrama con las cuatro partes de un texto informativo: titulo, introduccion, desarrollo y cierre."));
        recursoContenidoRepository.save(recurso(contenidos.get(1), TipoRecurso.IMAGEN,
                "recursos/infografia-ciclo-agua.png",
                "Infografia de seis recuadros con las etapas del ciclo del agua."));
        recursoContenidoRepository.save(recurso(contenidos.get(2), TipoRecurso.IMAGEN,
                "recursos/jerarquia-operaciones.png",
                "Organigrama que ordena las operaciones segun la accion que realizar."));
        recursoContenidoRepository.save(recurso(contenidos.get(3), TipoRecurso.IMAGEN,
                "recursos/patrones-figuras.png",
                "Franja con cinco figuras de una secuencia y la regla escrita al lado."));
        recursoContenidoRepository.save(recurso(contenidos.get(4), TipoRecurso.AUDIO,
                "recursos/auditivo-cambio-estado.mp3",
                "Transcripcion de una explicacion de 90 segundos sobre cambios de estado."));
        recursoContenidoRepository.save(recurso(contenidos.get(5), TipoRecurso.IMAGEN,
                "recursos/cadena-alimentaria.png",
                "Cadena de cuatro niveles con flechas que unen sol, planta, insecto y ave."));
        recursoContenidoRepository.save(recurso(contenidos.get(6), TipoRecurso.IMAGEN,
                "recursos/mapa-comuna-marcadores.png",
                "Mapa simple de la comuna con cuatro puntos de referencia y una leyenda."));
        recursoContenidoRepository.save(recurso(contenidos.get(7), TipoRecurso.AUDIO,
                "recursos/auditivo-entorno-comunitario.mp3",
                "Transcripcion de una entrevista breve a dos vecinos sobre su plaza."));
    }

    private RecursoContenido recurso(Contenido contenido, TipoRecurso tipo, String url, String descripcion) {
        return RecursoContenido.builder()
                .contenido(contenido)
                .tipo(tipo)
                .url(url)
                .descripcion(descripcion)
                .origen(OrigenMaterial.PROPIO)
                .build();
    }

    // ------------------------------------------------------------------
    // E) Actividades: 1 ACTIVIDAD, 1 DESAFIO y 1 PRUEBA_FINAL por unidad
    //    El indice 0..2 es la unidad 1, 3..5 la unidad 2, etc.
    // ------------------------------------------------------------------

    private List<Actividad> crearActividades(List<Unidad> unidades) {
        List<Actividad> actividades = new ArrayList<>();

        actividades.add(actividadRepository.save(actividad(unidades.get(0),
                "Explorar un texto informativo", TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(0),
                "Desafio: reconstruir un aviso", TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(0),
                "Prueba final de Lengua y Comunicacion", TipoActividad.PRUEBA_FINAL, 3)));

        actividades.add(actividadRepository.save(actividad(unidades.get(1),
                "Explorar problemas de la vida diaria", TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(1),
                "Desafio: encontrar el patron", TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(1),
                "Prueba final de Matematica", TipoActividad.PRUEBA_FINAL, 3)));

        actividades.add(actividadRepository.save(actividad(unidades.get(2),
                "Explorar los cambios de la materia", TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(2),
                "Desafio: ordenar una cadena", TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(2),
                "Prueba final de Ciencias Naturales", TipoActividad.PRUEBA_FINAL, 3)));

        actividades.add(actividadRepository.save(actividad(unidades.get(3),
                "Explorar la ubicacion de un hecho", TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(3),
                "Desafio: comparar dos lugares", TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(3),
                "Prueba final de Historia y Ciencias Sociales", TipoActividad.PRUEBA_FINAL, 3)));

        return actividades;
    }

    private Actividad actividad(Unidad unidad, String titulo, TipoActividad tipo, int orden) {
        return Actividad.builder()
                .unidad(unidad)
                .titulo(titulo)
                .tipo(tipo)
                .orden(orden)
                .build();
    }

    // ------------------------------------------------------------------
    // F) Preguntas y alternativas de la prueba final de la unidad 1
    // ------------------------------------------------------------------

    private BancoPreguntas crearPreguntas(Unidad unidad, Actividad pruebaFinal,
                                          ObjetivoAprendizaje oa1, ObjetivoAprendizaje oa2) {
        List<Pregunta> preguntas = new ArrayList<>();
        Map<String, Alternativa> alternativas = new LinkedHashMap<>();

        Pregunta p1 = preguntaRepository.save(pregunta(unidad, pruebaFinal, oa1,
                "Lee el siguiente texto sobre el ciclo del agua y responde: el texto informa principalmente sobre",
                "Responde en una frase. Basta con nombrar el tema del texto.", Dificultad.BAJA, 1));
        preguntas.add(p1);
        guardarAlternativas(p1, alternativas, "1",
                "las formas de empezar el dia",
                "el proceso por el que el agua recorre la tierra",
                "las plantas y su alimento",
                "las partes de un texto");
        marcarCorrecta(alternativas, "1-2");

        Pregunta p2 = preguntaRepository.save(pregunta(unidad, pruebaFinal, oa1,
                "Cual de estas partes de un texto informativo aparece primero y anuncia de que se tratara",
                "Marca una sola alternativa y explica en una linea por que.", Dificultad.BAJA, 2));
        preguntas.add(p2);
        guardarAlternativas(p2, alternativas, "2",
                "El desarrollo",
                "El titulo",
                "La conclusion");
        marcarCorrecta(alternativas, "2-2");

        Pregunta p3 = preguntaRepository.save(pregunta(unidad, pruebaFinal, oa2,
                "Observa la infografia que acompana al texto. Que dato aporta la imagen que el texto no repite",
                "Menciona un dato concreto de la imagen.", Dificultad.MEDIA, 1));
        preguntas.add(p3);
        guardarAlternativas(p3, alternativas, "3",
                "Un dato sobre etapas del agua que el texto no detalla",
                "El numero de palabras del texto",
                "El nombre de quien lo escribio");
        marcarCorrecta(alternativas, "3-1");

        Pregunta p4 = preguntaRepository.save(pregunta(unidad, pruebaFinal, oa2,
                "En una secuencia de figuras donde se repite un circulo cada cuatro elementos, que figura ocupara la posicion 10",
                "Escribe el numero de la posicion y el nombre de la figura.", Dificultad.MEDIA, 2));
        preguntas.add(p4);
        guardarAlternativas(p4, alternativas, "4",
                "Un cuadrado",
                "Un circulo",
                "Un triangulo");
        marcarCorrecta(alternativas, "4-2");

        Pregunta p5 = preguntaRepository.save(pregunta(unidad, pruebaFinal, oa1,
                "Un texto explica que las plantas usan la luz para fabricar su alimento. De que tipo de texto se trata",
                "Responde indicando el tipo y una caracteristica que lo delate.", Dificultad.ALTA, 1));
        preguntas.add(p5);
        guardarAlternativas(p5, alternativas, "5",
                "Un texto informativo, porque explica un proceso de la naturaleza",
                "Un texto poetico, porque usa imagenes",
                "Un articulo de opinion, porque critica");
        marcarCorrecta(alternativas, "5-1");

        Pregunta p6 = preguntaRepository.save(pregunta(unidad, pruebaFinal, oa2,
                "Dos personas leen el mismo texto pero sacan conclusiones distintas. Cual es la razon mas probable",
                "Explica usando la idea de que los mensajes combinan texto e imagen.", Dificultad.ALTA, 2));
        preguntas.add(p6);
        guardarAlternativas(p6, alternativas, "6",
                "Leyeron partes distintas del mensaje completo",
                "El texto estaba mal escrito",
                "No tenia imagenes");
        marcarCorrecta(alternativas, "6-1");

        return new BancoPreguntas(preguntas, alternativas);
    }

    private Pregunta pregunta(Unidad unidad, Actividad actividad, ObjetivoAprendizaje oa,
                              String enunciado, String criterio, Dificultad dificultad, int orden) {
        return Pregunta.builder()
                .actividad(actividad)
                .unidad(unidad)
                .oa(oa)
                .enunciado(enunciado)
                .criterioRespuesta(criterio)
                .dificultad(dificultad)
                .orden(orden)
                .build();
    }

    private void guardarAlternativas(Pregunta pregunta, Map<String, Alternativa> destino,
                                     String clavePregunta, String... textos) {
        for (int i = 0; i < textos.length; i++) {
            Alternativa alternativa = alternativaRepository.save(Alternativa.builder()
                    .pregunta(pregunta)
                    .texto(textos[i])
                    .esCorrecta(false)
                    .build());
            destino.put(clavePregunta + "-" + (i + 1), alternativa);
        }
    }

    private void marcarCorrecta(Map<String, Alternativa> alternativas, String clave) {
        Alternativa alternativa = alternativas.get(clave);
        alternativa.setEsCorrecta(true);
        alternativaRepository.save(alternativa);
    }

    // ------------------------------------------------------------------
    // G) Asignaciones del docente (1 por unidad, con prueba final habilitada)
    // ------------------------------------------------------------------

    private List<Asignacion> crearAsignaciones(Curso curso, List<Unidad> unidades, Usuario docente) {
        return List.of(
                asignacionRepository.save(asignacion(curso, unidades.get(0), docente, 2,
                        "2026-04-06T08:00:00Z")),
                asignacionRepository.save(asignacion(curso, unidades.get(1), docente, 2,
                        "2026-04-06T08:05:00Z")),
                asignacionRepository.save(asignacion(curso, unidades.get(2), docente, 1,
                        "2026-04-06T08:10:00Z")),
                asignacionRepository.save(asignacion(curso, unidades.get(3), docente, 1,
                        "2026-04-06T08:15:00Z"))
        );
    }

    private Asignacion asignacion(Curso curso, Unidad unidad, Usuario docente,
                                  int maxIntentos, String fecha) {
        return Asignacion.builder()
                .curso(curso)
                .unidad(unidad)
                .docente(docente)
                .maxIntentos(maxIntentos)
                .pruebaFinalHabilitada(true)
                .fecha(Instant.parse(fecha))
                .build();
    }

    private void crearAsignacionesOa(List<Asignacion> asignaciones, List<ObjetivoAprendizaje> oas) {
        for (int i = 0; i < asignaciones.size(); i++) {
            Asignacion asignacion = asignaciones.get(i);
            for (int j = 0; j < 2; j++) {
                ObjetivoAprendizaje oa = oas.get(i * 2 + j);
                asignacionOaRepository.save(AsignacionOa.builder()
                        .id(AsignacionOaId.builder()
                                .asignacionId(asignacion.getId())
                                .oaId(oa.getId())
                                .build())
                        .asignacion(asignacion)
                        .objetivoAprendizaje(oa)
                        .unidadId(asignacion.getUnidad().getId())
                        .build());
            }
        }
    }

    private void crearAsignacionesActividad(List<Asignacion> asignaciones, List<Actividad> actividades) {
        for (int i = 0; i < asignaciones.size(); i++) {
            Asignacion asignacion = asignaciones.get(i);
            for (int j = 0; j < 3; j++) {
                Actividad actividad = actividades.get(i * 3 + j);
                asignacionActividadRepository.save(AsignacionActividad.builder()
                        .id(AsignacionActividadId.builder()
                                .asignacionId(asignacion.getId())
                                .actividadId(actividad.getId())
                                .build())
                        .asignacion(asignacion)
                        .actividad(actividad)
                        .unidadId(asignacion.getUnidad().getId())
                        .build());
            }
        }
    }

    // ------------------------------------------------------------------
    // H) Intentos y respuestas de ejemplo
    //    Camila (2 intentos, el ultimo completado), Mateo (1), Javiera
    //    (en progreso) y Benjamin (sin intentos).
    // ------------------------------------------------------------------

    private Intentos crearIntentos(Usuarios usuarios, Actividad pruebaFinal) {
        // El primer intento de Camila queda registrado pero no se usa en el
        // informe: la regla toma el ultimo intento completado.
        intentoRepository.save(intento(usuarios.camila(), pruebaFinal, 1,
                "2026-05-04T10:00:00Z", "2026-05-04T10:14:00Z"));
        Intento camila2 = intentoRepository.save(intento(usuarios.camila(), pruebaFinal, 2,
                "2026-05-06T15:20:00Z", "2026-05-06T15:33:00Z"));
        Intento mateo = intentoRepository.save(intento(usuarios.mateo(), pruebaFinal, 1,
                "2026-05-05T11:00:00Z", "2026-05-05T11:19:00Z"));
        Intento javiera = intentoRepository.save(intento(usuarios.javiera(), pruebaFinal, 1,
                "2026-05-07T16:00:00Z", null));

        return new Intentos(camila2, mateo, javiera);
    }

    private Intento intento(Usuario estudiante, Actividad actividad, int numero,
                            String inicio, String fin) {
        return Intento.builder()
                .estudiante(estudiante)
                .actividad(actividad)
                .numero(numero)
                .fechaInicio(Instant.parse(inicio))
                .fechaFin(fin == null ? null : Instant.parse(fin))
                .build();
    }

    /**
     * Respuestas del ultimo intento completado de Camila (2/3 en el OA 1 y
     * 1/3 en el OA 2) y del intento de Mateo. es_correcta se deriva de la
     * alternativa elegida, igual que lo hace RespuestaEstudianteService.
     */
    private void crearRespuestas(Intentos intentos, BancoPreguntas banco) {
        // Camila: responde mal las preguntas 4, 5 y 6.
        responder(intentos.camila(), banco, 1, 2);
        responder(intentos.camila(), banco, 2, 2);
        responder(intentos.camila(), banco, 3, 1);
        responder(intentos.camila(), banco, 4, 1);
        responder(intentos.camila(), banco, 5, 2);
        responder(intentos.camila(), banco, 6, 3);

        // Mateo: le cuesta el OA 1 y le va mejor el OA 2.
        responder(intentos.mateo(), banco, 1, 1);
        responder(intentos.mateo(), banco, 2, 1);
        responder(intentos.mateo(), banco, 3, 1);
        responder(intentos.mateo(), banco, 4, 2);
        responder(intentos.mateo(), banco, 5, 1);
        responder(intentos.mateo(), banco, 6, 2);
    }

    private void responder(Intento intento, BancoPreguntas banco, int numeroPregunta, int numeroAlternativa) {
        Pregunta pregunta = banco.preguntas().get(numeroPregunta - 1);
        Alternativa alternativa = banco.alternativas()
                .get(numeroPregunta + "-" + numeroAlternativa);

        respuestaEstudianteRepository.save(RespuestaEstudiante.builder()
                .intento(intento)
                .pregunta(pregunta)
                .alternativaId(alternativa.getId())
                .esCorrecta(alternativa.getEsCorrecta())
                .build());
    }

    // ------------------------------------------------------------------
    // I) Informe de ejemplo de Camila en la unidad 1
    //    Snapshot congelado del ultimo intento completado (seccion 5):
    //      OA 1 -> 2 correctas de 3 -> REFUERZO
    //      OA 2 -> 1 correcta de 3  -> REFUERZO
    // ------------------------------------------------------------------

    private void crearInforme(Usuarios usuarios, Unidad unidad, List<ObjetivoAprendizaje> oas) {
        InformePedagogico informe = informePedagogicoRepository.save(InformePedagogico.builder()
                .estudiante(usuarios.camila())
                .unidad(unidad)
                .docente(usuarios.docente())
                .fechaGeneracion(Instant.parse("2026-05-08T09:00:00Z"))
                .estado(EstadoInforme.PENDIENTE_VALIDACION)
                .fechaValidacion(null)
                .build());

        detalleInformeOaRepository.save(detalle(informe, oas.get(0), 3, 2, NivelOa.REFUERZO));
        detalleInformeOaRepository.save(detalle(informe, oas.get(1), 3, 1, NivelOa.REFUERZO));
    }

    private DetalleInformeOa detalle(InformePedagogico informe, ObjetivoAprendizaje oa,
                                     int total, int correctas, NivelOa nivel) {
        return DetalleInformeOa.builder()
                .informe(informe)
                .oa(oa)
                .totalPreguntas(total)
                .correctas(correctas)
                .nivel(nivel)
                .build();
    }

    // ------------------------------------------------------------------
    // Estructuras auxiliares del seed
    // ------------------------------------------------------------------

    private record Usuarios(Usuario utp, Usuario docente, Usuario camila,
                            Usuario mateo, Usuario javiera, Usuario benjamin) {
    }

    private record Intentos(Intento camila, Intento mateo, Intento javiera) {
    }

    private record BancoPreguntas(List<Pregunta> preguntas, Map<String, Alternativa> alternativas) {
    }
}
