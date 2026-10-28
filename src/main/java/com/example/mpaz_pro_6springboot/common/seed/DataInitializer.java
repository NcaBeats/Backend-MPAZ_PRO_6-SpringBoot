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
import com.example.mpaz_pro_6springboot.docencia.docencia.model.DocenteCursoAsignatura;
import com.example.mpaz_pro_6springboot.docencia.docencia.repository.DocenteCursoAsignaturaRepository;
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
import java.util.List;

/**
 * Carga los datos de demo alineados con el seed local de la app:
 * una unidad por asignatura, cuatro unidades, una de ellas en BORRADOR.
 *
 * La UTP conserva el acceso a todo. El docente puede crear material en la
 * unidad de Historia y enviarla a revisión. El estudiante solo ve las unidades
 * publicadas y asignadas a su curso.
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
    private final DocenteCursoAsignaturaRepository docenciaRepository;
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

        log.info("DataInitializer: datos de demo cargados (4 unidades, 36 OAs, 12 actividades, 20 preguntas, 1 informe)");
    }

    private void seed() {
        Curso curso6A = cursoRepository.save(Curso.builder()
                .nombre("6A")
                .anio(2026)
                .build());
        Curso curso6B = cursoRepository.save(Curso.builder()
                .nombre("6B")
                .anio(2026)
                .build());

        Usuarios usuarios = crearUsuarios(curso6A, curso6B);
        List<Asignatura> asignaturas = crearAsignaturas();
        crearDocencias(usuarios.docente(), List.of(curso6A, curso6B), asignaturas);
        List<Unidad> unidades = crearUnidades(asignaturas, usuarios.utp());
        List<ObjetivoAprendizaje> oas = crearObjetivos(unidades);
        List<Contenido> contenidos = crearContenidos(oas);
        crearRecursos(contenidos);

        List<Actividad> actividades = crearActividades(unidades, contenidos);
        BancoPreguntas banco = crearPreguntas(actividades, oas);

        List<Asignacion> asignaciones = crearAsignaciones(
                List.of(curso6A, curso6B),
                unidades
        );
        crearAsignacionesOa(asignaciones, oas);
        crearAsignacionesActividad(asignaciones, actividades);

        Intento camila1 = intentoRepository.save(intento(usuarios.camila(), actividades.get(2), 1,
                "2026-05-04T10:00:00Z", "2026-05-04T10:14:00Z"));
        Intento camila2 = intentoRepository.save(intento(usuarios.camila(), actividades.get(2), 2,
                "2026-05-06T15:20:00Z", "2026-05-06T15:33:00Z"));
        Intento mateo1 = intentoRepository.save(intento(usuarios.mateo(), actividades.get(5), 1,
                "2026-05-05T11:00:00Z", "2026-05-05T11:19:00Z"));
        Intento javiera1 = intentoRepository.save(intento(usuarios.javiera(), actividades.get(8), 1,
                "2026-05-07T16:00:00Z", null));

        crearRespuestas(camila1, camila2, mateo1, banco, actividades);
        crearInforme(usuarios, unidades.get(0), oas);
    }

    private Usuarios crearUsuarios(Curso curso6A, Curso curso6B) {
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

        Usuario camila = usuarioRepository.save(estudiante("Camila Rivas", "camila", hashEstudiante, curso6A));
        Usuario mateo = usuarioRepository.save(estudiante("Mateo Cortes", "mateo", hashEstudiante, curso6A));
        Usuario javiera = usuarioRepository.save(estudiante("Javiera Munoz", "javiera", hashEstudiante, curso6A));
        Usuario benjamin = usuarioRepository.save(estudiante("Benjamin Ortiz", "benjamin", hashEstudiante, curso6A));

        usuarioRepository.save(estudiante("Sofia Herrera", "sofia", hashEstudiante, curso6B));
        usuarioRepository.save(estudiante("Tomas Vega", "tomas", hashEstudiante, curso6B));
        usuarioRepository.save(estudiante("Antonia Paredes", "antonia", hashEstudiante, curso6B));
        usuarioRepository.save(estudiante("Vicente Silva", "vicente", hashEstudiante, curso6B));

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
                        .titulo("Lectura - Comprension")
                        .orden(1)
                        .estado(EstadoContenido.PUBLICADO)
                        .autorizadaPor(utp)
                        .fechaAutorizacion(Instant.ofEpochMilli(1772409600000L))
                        .build()),
                unidadRepository.save(Unidad.builder()
                        .asignatura(asignaturas.get(1))
                        .titulo("Numeros y operaciones")
                        .orden(1)
                        .estado(EstadoContenido.PUBLICADO)
                        .autorizadaPor(utp)
                        .fechaAutorizacion(Instant.ofEpochMilli(1772409600000L))
                        .build()),
                unidadRepository.save(Unidad.builder()
                        .asignatura(asignaturas.get(2))
                        .titulo("Ciencias de la Vida")
                        .orden(1)
                        .estado(EstadoContenido.PUBLICADO)
                        .autorizadaPor(utp)
                        .fechaAutorizacion(Instant.ofEpochMilli(1772668800000L))
                        .build()),
                unidadRepository.save(Unidad.builder()
                        .asignatura(asignaturas.get(3))
                        .titulo("Historia")
                        .orden(1)
                        .estado(EstadoContenido.BORRADOR)
                        .build())
        );
    }

    private List<ObjetivoAprendizaje> crearObjetivos(List<Unidad> unidades) {
        List<ObjetivoAprendizaje> oas = new ArrayList<>();

        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 01",
                "Leer de manera fluida textos variados apropiados a su edad: pronunciando las palabras con precisión; respetando la prosodia indicada por todos los signos de puntuación; decodificando de manera automática la mayoría de las palabras del texto.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 02",
                "Comprender textos aplicando estrategias de comprensión lectora; por ejemplo: relacionar la información del texto con sus experiencias y conocimientos; releer lo que no fue comprendido; formular preguntas sobre lo leído y responderlas; organizar la información en esquemas o mapas conceptuales; resumir.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 03",
                "Leer y familiarizarse con un amplio repertorio de literatura para aumentar su conocimiento del mundo, desarrollar su imaginación y reconocer su valor social y cultural; por ejemplo: poemas, cuentos folclóricos y de autor, fábulas, leyendas, mitos, novelas, historietas, entre otros.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 04",
                "Analizar aspectos relevantes de las narraciones leídas para profundizar su comprensión: identificar las acciones principales del relato y explicar cómo influyen en el desarrollo de la historia; explicar las actitudes y reacciones de los personajes de acuerdo con sus motivaciones y las situaciones que viven; describir el ambiente y las costumbres representadas en el texto y explicar su influencia en las acciones del relato; relacionar el relato, si es pertinente, con la época y el lugar en que se ambienta; interpretar el lenguaje figurado; expresar opiniones sobre las actitudes y acciones de los personajes y fundamentarlas con ejemplos del texto; llegar a conclusiones sustentadas en la información del texto; comparar textos de autores diferentes y justificar su preferencia por alguno.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 05",
                "Analizar aspectos relevantes de diversos poemas para profundizar su comprensión: explicar cómo el lenguaje poético empleado por el autor apela a los sentidos, sugiere estados de ánimo y crea imágenes en el lector; identificar personificaciones, comparaciones e hipérboles y explicar su significado dentro del poema; analizar cómo los efectos sonoros (aliteración y onomatopeya) utilizados por el poeta refuerzan lo dicho.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 06",
                "Leer independientemente y comprender textos no literarios (cartas, biografías, relatos históricos, libros y artículos informativos, noticias, etc.) para ampliar su conocimiento del mundo y formarse una opinión: extraer información explícita e implícita; hacer inferencias a partir de la información del texto y de sus experiencias y conocimientos; relacionar la información de imágenes, gráficos, tablas, mapas o diagramas con el texto; interpretar expresiones en lenguaje figurado; comparar información entre dos textos del mismo tema; formular una opinión sobre algún aspecto de la lectura y fundamentarla con información del texto o conocimientos previos.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 07",
                "Evaluar críticamente la información presente en textos de diversa procedencia: determinar quién es el emisor, cuál es su propósito y a quién dirige el mensaje; evaluar si un texto entrega suficiente información para responder una pregunta o cumplir un propósito; comparar la información que se entrega sobre una misma noticia en distintas fuentes.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 08",
                "Sintetizar, registrar y ordenar las ideas principales de textos leídos para satisfacer propósitos como estudiar, hacer una investigación, recordar detalles, entre otros.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 09",
                "Desarrollar el gusto por la lectura, leyendo habitualmente diversos textos.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 10",
                "Asistir habitualmente a la biblioteca para satisfacer diversos propósitos (seleccionar textos, investigar sobre un tema, informarse sobre actualidad, etc.), adecuando su comportamiento y cuidando el material para permitir el trabajo y la lectura de los demás.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 11",
                "Buscar y comparar información sobre un tema, utilizando fuentes como internet, enciclopedias, libros y prensa, entre otras, para llevar a cabo una investigación.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(0), "LE06 OA 12",
                "Aplicar estrategias para determinar el significado de palabras nuevas: claves contextuales; raíces y afijos; preguntar a otro; diccionarios, enciclopedias e internet.",
                "Lectura - Comprensión")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 01",
                "Demostrar que comprenden los factores y múltiplos: determinar los múltiplos y factores de números naturales menores de 100; identificar números primos y compuestos; resolver problemas que involucran múltiplos.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 02",
                "Realizar cálculos que involucren las cuatro operaciones en el contexto de la resolución de problemas, utilizando la calculadora en ámbitos superiores a 10 000.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 03",
                "Demostrar que comprenden el concepto de razón de manera concreta, pictórica y simbólica, en forma manual y/o usando software educativo.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 04",
                "Demostrar que comprenden el concepto de porcentaje de manera concreta, pictórica y simbólica, de forma manual y/o usando software educativo.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 05",
                "Demostrar que comprenden las fracciones y números mixtos: identificar y determinar equivalencias entre fracciones impropias y números mixtos usando material concreto y representaciones pictóricas, de manera manual y/o con software educativo; representar estos números en la recta numérica.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 06",
                "Resolver adiciones y sustracciones de fracciones propias e impropias y números mixtos con numeradores y denominadores de hasta dos dígitos.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 07",
                "Demostrar que comprenden la multiplicación y la división de decimales por números naturales de un dígito, múltiplos de 10 y decimales hasta la milésima, de manera concreta, pictórica y simbólica.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(1), "MA06 OA 08",
                "Resolver problemas rutinarios y no rutinarios que involucren adiciones y sustracciones de fracciones propias, impropias, números mixtos o decimales hasta la milésima.",
                "Números y operaciones")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(2), "CN06 OA 01",
                "Explicar, a partir de una investigación experimental, los requerimientos de agua, dióxido de carbono y energía lumínica para la producción de azúcar y liberación de oxígeno en la fotosíntesis, comunicando sus resultados y los aportes de científicos en este campo a través del tiempo.",
                "Ciencias de la Vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(2), "CN06 OA 02",
                "Representar, por medio de modelos, la transferencia de energía y materia desde los organismos fotosintéticos a otros seres vivos por medio de cadenas y redes alimentarias en diferentes ecosistemas.",
                "Ciencias de la Vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(2), "CN06 OA 03",
                "Analizar los efectos de la actividad humana sobre las redes alimentarias.",
                "Ciencias de la Vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(2), "CN06 OA 04",
                "Identificar y describir las funciones de las principales estructuras del sistema reproductor humano femenino y masculino.",
                "Ciencias de la Vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(2), "CN06 OA 05",
                "Describir y comparar los cambios que se producen en la pubertad en mujeres y hombres, reconociéndola como una etapa del desarrollo humano.",
                "Ciencias de la Vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(2), "CN06 OA 06",
                "Reconocer los beneficios de realizar actividad física en forma regular y de cuidar la higiene corporal en el período de la pubertad.",
                "Ciencias de la Vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(2), "CN06 OA 07",
                "Investigar y comunicar los efectos nocivos de algunas drogas para la salud, proponiendo conductas de protección.",
                "Ciencias de la Vida")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 01",
                "Explicar los múltiples antecedentes de la independencia de las colonias americanas y reconocer que la independencia de Chile se enmarca en un proceso continental.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 02",
                "Explicar el desarrollo del proceso de independencia de Chile, considerando actores y bandos que se enfrentaron, hombres y mujeres destacados, avances y retrocesos de la causa patriota y acontecimientos significativos, como el cabildo abierto de 1810 y la formación de la Primera Junta Nacional de Gobierno, la elección del primer Congreso Nacional, las batallas de Rancagua, Chacabuco y Maipú y la Declaración de la Independencia, entre otros.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 03",
                "Describir hitos y procesos de la organización de la república, incluyendo las dificultades y desafíos que implicó organizar en Chile una nueva forma de gobierno, el surgimiento de grupos con diferentes ideas políticas (conservadores y liberales), las características de la Constitución de 1833 y el impacto de las reformas realizadas por los liberales en la segunda mitad del siglo XIX.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 04",
                "Investigar aspectos culturales del siglo XIX, como los avances en educación y la fundación de instituciones, el aporte de intelectuales y científicos nacionales (por ejemplo, Diego Barros Arana, Benjamín Vicuña Mackenna y José Victorino Lastarria) y extranjeros (por ejemplo, Andrés Bello, Claudio Gay, Charles Darwin y María Graham), las primeras mujeres en obtener títulos universitarios y el impacto de la llegada del ferrocarril y otros avances tecnológicos.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 05",
                "Describir cómo se conformó el territorio de Chile durante el siglo XIX, considerando colonizaciones europeas, la incorporación de Isla de Pascua, la ocupación de la Araucanía, la Guerra del Pacífico y diversos conflictos bélicos, entre otros factores.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 06",
                "Caracterizar los principales aspectos que definieron el período de riqueza aportada por la explotación del salitre, considerando la expansión económica y el inicio de la cuestión social.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 07",
                "Explicar y dar ejemplos de la progresiva democratización de la sociedad durante el siglo XX, considerando el acceso creciente al voto, la participación de la mujer en la vida pública y el acceso a la educación y la cultura, entre otros.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 08",
                "Comparar diferentes visiones sobre el quiebre de la democracia en Chile, el régimen o dictadura militar y el proceso de recuperación de la democracia a fines del siglo XX, considerando los distintos actores, experiencias y puntos de vista, y el consenso actual con respecto al valor de la democracia.",
                "Historia")));
        oas.add(objetivoAprendizajeRepository.save(oa(
                unidades.get(3), "HI06 OA 09",
                "Explicar y dar ejemplos de aspectos que se mantienen y aspectos que han cambiado o se han desarrollado en la sociedad chilena a lo largo de su historia.",
                "Historia")));

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

    private List<Contenido> crearContenidos(List<ObjetivoAprendizaje> oas) {
        List<Contenido> contenidos = new ArrayList<>();

        contenidos.add(contenidoRepository.save(contenido(oas.get(1),
                "Estrategias de comprensión lectora",
                "Comprender un texto requiere construir significado: conectar sus ideas con conocimientos previos, releer pasajes poco claros, formular preguntas y resumir la información central. Un esquema ayuda a separar las ideas principales de los detalles.",
                "Al leer una noticia sobre el agua, se puede resumir la idea central y anotar dos datos que la apoyan.",
                "Lee un texto breve. Formula una pregunta, responde con información del texto y escribe un resumen de dos oraciones.")));
        contenidos.add(contenidoRepository.save(contenido(oas.get(15),
                "Comprender y calcular porcentajes",
                "Un porcentaje expresa una cantidad como parte de 100. Puede representarse con una cuadrícula de cien partes, como fracción con denominador 100 o con un número seguido del símbolo %.",
                "Si 25 de 100 casillas están marcadas, está representado el 25 %. En una encuesta de 20 estudiantes, 5 equivalen al 25 %.",
                "Representa 10 %, 25 % y 50 % de una cuadrícula de cien. Luego resuelve un problema sencillo e indica cómo encontraste el resultado.")));
        contenidos.add(contenidoRepository.save(contenido(oas.get(21),
                "Cadenas y redes alimentarias",
                "La materia y la energía pasan de los organismos fotosintéticos a otros seres vivos a través de la alimentación. Las flechas de una cadena alimentaria indican la dirección de la transferencia de materia y energía.",
                "Hierba → saltamontes → rana → culebra muestra una posible relación alimentaria en un ecosistema.",
                "Construye una cadena alimentaria de un ecosistema, identifica al productor y explica qué podría ocurrir si uno de sus organismos disminuye.")));
        contenidos.add(contenidoRepository.save(contenido(oas.get(28),
                "El proceso de independencia de Chile",
                "La independencia de Chile fue un proceso con avances y retrocesos, protagonizado por distintos grupos y personas. Entre sus hitos están el cabildo abierto de 1810, la Primera Junta de Gobierno, las batallas de Rancagua, Chacabuco y Maipú y la Declaración de Independencia.",
                "Una línea de tiempo permite ordenar los hitos y relacionarlos con los cambios en el control político del territorio.",
                "Ordena tres hitos del proceso de independencia y explica por qué uno de ellos fue importante para la causa patriota.")));

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
                .estado(oa.getUnidad().getEstado())
                .build();
    }

    private void crearRecursos(List<Contenido> contenidos) {
        recursoContenidoRepository.save(recurso(contenidos.get(0), TipoRecurso.IMAGEN,
                "recursos/6basico/eje-01-oa.png",
                "Recurso visual de apoyo para estrategias de comprensión lectora, relacionado con LE06 OA 02."));
        recursoContenidoRepository.save(recurso(contenidos.get(1), TipoRecurso.IMAGEN,
                "recursos/6basico/eje-04-oa.png",
                "Recurso visual de apoyo para comprender y calcular porcentajes, relacionado con MA06 OA 04."));
        recursoContenidoRepository.save(recurso(contenidos.get(2), TipoRecurso.IMAGEN,
                "recursos/6basico/eje-09-oa.png",
                "Recurso visual de apoyo para cadenas y redes alimentarias, relacionado con CN06 OA 02."));
        recursoContenidoRepository.save(recurso(contenidos.get(3), TipoRecurso.IMAGEN,
                "recursos/6basico/eje-12-oa.png",
                "Recurso visual de apoyo para el proceso de independencia de chile, relacionado con HI06 OA 02."));
    }

    private RecursoContenido recurso(Contenido contenido, TipoRecurso tipo, String url, String descripcion) {
        return RecursoContenido.builder()
                .contenido(contenido)
                .tipo(tipo)
                .url(url)
                .descripcion(descripcion)
                .origen(OrigenMaterial.PROPIO)
                .estado(contenido.getEstado())
                .build();
    }

    private List<Actividad> crearActividades(List<Unidad> unidades, List<Contenido> contenidos) {
        List<Actividad> actividades = new ArrayList<>();

        actividades.add(actividadRepository.save(actividad(unidades.get(0),
                contenidos.get(0), "Actividad: Estrategias de comprensión lectora",
                TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(0),
                contenidos.get(0), "Desafío: aplicar estrategias de comprensión lectora",
                TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(0),
                null, "Evaluación: Lectura - Comprensión",
                TipoActividad.PRUEBA_FINAL, 3)));
        actividades.add(actividadRepository.save(actividad(unidades.get(1),
                contenidos.get(1), "Actividad: Comprender y calcular porcentajes",
                TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(1),
                contenidos.get(1), "Desafío: aplicar comprender y calcular porcentajes",
                TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(1),
                null, "Evaluación: Números y operaciones",
                TipoActividad.PRUEBA_FINAL, 3)));
        actividades.add(actividadRepository.save(actividad(unidades.get(2),
                contenidos.get(2), "Actividad: Cadenas y redes alimentarias",
                TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(2),
                contenidos.get(2), "Desafío: aplicar cadenas y redes alimentarias",
                TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(2),
                null, "Evaluación: Ciencias de la Vida",
                TipoActividad.PRUEBA_FINAL, 3)));
        actividades.add(actividadRepository.save(actividad(unidades.get(3),
                contenidos.get(3), "Actividad: El proceso de independencia de Chile",
                TipoActividad.ACTIVIDAD, 1)));
        actividades.add(actividadRepository.save(actividad(unidades.get(3),
                contenidos.get(3), "Desafío: aplicar el proceso de independencia de chile",
                TipoActividad.DESAFIO, 2)));
        actividades.add(actividadRepository.save(actividad(unidades.get(3),
                null, "Evaluación: Historia",
                TipoActividad.PRUEBA_FINAL, 3)));

        return actividades;
    }

    private Actividad actividad(Unidad unidad, Contenido contenido, String titulo,
                                TipoActividad tipo, int orden) {
        return Actividad.builder()
                .unidad(unidad)
                .contenido(contenido)
                .titulo(titulo)
                .tipo(tipo)
                .orden(orden)
                .build();
    }

    private BancoPreguntas crearPreguntas(
            List<Actividad> actividades,
            List<ObjetivoAprendizaje> oas
    ) {
        List<PreguntaCreada> preguntas = new ArrayList<>();

        crearPregunta(preguntas, actividades.get(0),
                oas.get(1), "¿Qué conviene hacer cuando una parte del texto no se comprende?",
                "Indica una estrategia de comprensión lectora.", Dificultad.BAJA, 1, 1,
                "Continuar sin prestar atención", "Releer el pasaje y formular una pregunta", "Mirar solo la última palabra");
        crearPregunta(preguntas, actividades.get(1),
                oas.get(1), "¿Para qué sirve resumir un texto?",
                "Relaciona el resumen con las ideas principales.", Dificultad.BAJA, 2, 2,
                "Para copiar todas las oraciones", "Para reemplazar el título por una imagen", "Para registrar sus ideas principales de forma breve");
        crearPregunta(preguntas, actividades.get(2),
                oas.get(1), "¿Qué debe incluir un buen resumen?",
                "Reconoce selección de información central.", Dificultad.MEDIA, 3, 0,
                "Las ideas principales del texto", "Opiniones que no aparecen en el texto", "Todas las palabras en el mismo orden");
        crearPregunta(preguntas, actividades.get(2),
                oas.get(1), "Si una respuesta no aparece escrita literalmente, ¿qué puede hacer el lector?",
                "Explica que puede inferir a partir de pistas y conocimientos previos.", Dificultad.MEDIA, 4, 1,
                "Inventar un dato sin revisar el texto", "Relacionar pistas del texto con sus conocimientos", "Ignorar la pregunta");
        crearPregunta(preguntas, actividades.get(2),
                oas.get(1), "¿Cuál estrategia ayuda a comprobar una interpretación?",
                "Selecciona una estrategia basada en evidencia textual.", Dificultad.ALTA, 5, 0,
                "Buscar pistas y releer la parte pertinente", "Elegir la respuesta más larga", "Cambiar de tema");
        crearPregunta(preguntas, actividades.get(3),
                oas.get(15), "¿Qué representa 25 %?",
                "Interpreta porcentaje como parte de 100.", Dificultad.BAJA, 1, 0,
                "25 partes de cada 100", "25 partes de cada 10", "100 partes de cada 25");
        crearPregunta(preguntas, actividades.get(4),
                oas.get(15), "¿Cuánto es el 50 % de 20?",
                "Calcula la mitad de una cantidad.", Dificultad.BAJA, 2, 1,
                "5", "10", "15");
        crearPregunta(preguntas, actividades.get(5),
                oas.get(15), "En una encuesta de 100 personas, 30 prefieren leer. ¿Qué porcentaje corresponde?",
                "Relaciona cantidad sobre 100 con porcentaje.", Dificultad.MEDIA, 3, 2,
                "3 %", "70 %", "30 %");
        crearPregunta(preguntas, actividades.get(5),
                oas.get(15), "¿Qué representación corresponde al 25 % de una cuadrícula de 100 casillas?",
                "Identifica 25 de 100 casillas.", Dificultad.MEDIA, 4, 0,
                "25 casillas marcadas", "50 casillas marcadas", "75 casillas marcadas");
        crearPregunta(preguntas, actividades.get(5),
                oas.get(15), "Una mochila cuesta $20 000 y tiene un descuento del 10 %. ¿Cuánto se descuenta?",
                "Calcula 10 % de 20 000.", Dificultad.ALTA, 5, 1,
                "$10 000", "$2 000", "$200");
        crearPregunta(preguntas, actividades.get(6),
                oas.get(21), "¿Qué organismos incorporan energía lumínica a muchas cadenas alimentarias?",
                "Identifica productores fotosintéticos.", Dificultad.BAJA, 1, 0,
                "Los productores fotosintéticos", "Solo los consumidores terciarios", "Los descomponedores exclusivamente");
        crearPregunta(preguntas, actividades.get(7),
                oas.get(21), "¿Qué indican las flechas en una cadena alimentaria?",
                "Interpreta transferencia de materia y energía.", Dificultad.BAJA, 2, 1,
                "El tamaño de los organismos", "La dirección de transferencia de materia y energía", "La distancia entre hábitats");
        crearPregunta(preguntas, actividades.get(8),
                oas.get(21), "En hierba → saltamontes → rana, ¿qué organismo es consumidor primario?",
                "Reconoce quién se alimenta del productor.", Dificultad.MEDIA, 3, 2,
                "La hierba", "La rana", "El saltamontes");
        crearPregunta(preguntas, actividades.get(8),
                oas.get(21), "¿Qué podría suceder si disminuye mucho la población de productores?",
                "Relaciona productores con disponibilidad de alimento y energía.", Dificultad.MEDIA, 4, 0,
                "Podría reducirse el alimento para otros niveles", "Todas las poblaciones aumentarían necesariamente", "La red alimentaria no tendría ningún cambio");
        crearPregunta(preguntas, actividades.get(8),
                oas.get(21), "¿Por qué las redes alimentarias representan mejor algunas relaciones que una sola cadena?",
                "Reconoce múltiples relaciones tróficas.", Dificultad.ALTA, 5, 0,
                "Muestran varias relaciones alimentarias conectadas", "Solo muestran un organismo", "Eliminan los productores");
        crearPregunta(preguntas, actividades.get(9),
                oas.get(28), "¿Qué hito ocurrió en Chile en 1810?",
                "Reconoce la Primera Junta/cabildo abierto.", Dificultad.BAJA, 1, 2,
                "La batalla de Maipú", "La Declaración de Independencia de 1818", "El cabildo abierto y la formación de la Primera Junta de Gobierno");
        crearPregunta(preguntas, actividades.get(10),
                oas.get(28), "¿Por qué se considera que la independencia de Chile fue un proceso?",
                "Reconoce avances, retrocesos y distintos hitos.", Dificultad.BAJA, 2, 0,
                "Porque ocurrió mediante distintos acontecimientos y etapas", "Porque ocurrió en un único día sin conflictos", "Porque no participaron personas");
        crearPregunta(preguntas, actividades.get(11),
                oas.get(28), "¿Qué permite mostrar una línea de tiempo?",
                "Organiza acontecimientos cronológicamente.", Dificultad.MEDIA, 3, 1,
                "La altura de las montañas", "El orden temporal de los acontecimientos", "Solo la población actual");
        crearPregunta(preguntas, actividades.get(11),
                oas.get(28), "¿Cuál de estos hitos se relaciona con la causa patriota?",
                "Identifica batalla vinculada al proceso.", Dificultad.MEDIA, 4, 2,
                "La formación de la ONU", "La llegada del ferrocarril en el siglo XIX", "La batalla de Chacabuco");
        crearPregunta(preguntas, actividades.get(11),
                oas.get(28), "¿Qué enfoque ayuda a explicar el proceso de independencia?",
                "Considera actores, bandos y acontecimientos.", Dificultad.ALTA, 5, 0,
                "Analizar actores, bandos, avances, retrocesos e hitos", "Mencionar solo una fecha sin contexto", "Excluir los acontecimientos políticos");

        return new BancoPreguntas(preguntas);
    }

    private void crearPregunta(
            List<PreguntaCreada> preguntas,
            Actividad actividad,
            ObjetivoAprendizaje oa,
            String enunciado,
            String criterio,
            Dificultad dificultad,
            int orden,
            int indiceCorrecta,
            String... textos
    ) {
        Pregunta pregunta = preguntaRepository.save(Pregunta.builder()
                .actividad(actividad)
                .unidad(actividad.getUnidad())
                .oa(oa)
                .enunciado(enunciado)
                .criterioRespuesta(criterio)
                .dificultad(dificultad)
                .orden(orden)
                .build());

        List<Alternativa> alternativas = new ArrayList<>();
        for (int i = 0; i < textos.length; i++) {
            Alternativa alternativa = alternativaRepository.save(Alternativa.builder()
                    .pregunta(pregunta)
                    .texto(textos[i])
                    .esCorrecta(i == indiceCorrecta)
                    .build());
            alternativas.add(alternativa);
        }
        preguntas.add(new PreguntaCreada(pregunta, alternativas));
    }

    private void crearDocencias(
            Usuario docente,
            List<Curso> cursos,
            List<Asignatura> asignaturas
    ) {
        for (Curso curso : cursos) {
            for (Asignatura asignatura : asignaturas) {
                docenciaRepository.save(DocenteCursoAsignatura.builder()
                        .docente(docente)
                        .curso(curso)
                        .asignatura(asignatura)
                        .build());
            }
        }
    }

    private List<Asignacion> crearAsignaciones(
            List<Curso> cursos,
            List<Unidad> unidades
    ) {
        List<Asignacion> asignaciones = new ArrayList<>();
        Instant[] fechas = {
                Instant.parse("2026-04-06T08:00:00Z"),
                Instant.parse("2026-04-06T08:01:00Z"),
                Instant.parse("2026-04-06T08:02:00Z"),
                Instant.parse("2026-04-06T08:03:00Z"),
                Instant.parse("2026-04-06T08:04:00Z"),
                Instant.parse("2026-04-06T08:05:00Z")
        };
        int index = 0;
        for (Curso curso : cursos) {
            for (int unitIndex = 0; unitIndex < 3; unitIndex++) {
                asignaciones.add(asignacionRepository.save(Asignacion.builder()
                        .curso(curso)
                        .unidad(unidades.get(unitIndex))
                        .maxIntentos(unitIndex == 2 ? 1 : 2)
                        .pruebaFinalHabilitada(true)
                        .fecha(fechas[index])
                        .build()));
                index++;
            }
        }
        return asignaciones;
    }

    private void crearAsignacionesOa(List<Asignacion> asignaciones, List<ObjetivoAprendizaje> oas) {
        int[] indices = {1, 15, 21};
        for (int i = 0; i < asignaciones.size(); i++) {
            Asignacion asignacion = asignaciones.get(i);
            ObjetivoAprendizaje oa = oas.get(indices[i % indices.length]);
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

    private void crearAsignacionesActividad(List<Asignacion> asignaciones, List<Actividad> actividades) {
        int[][] indices = {{0, 1}, {3, 4}, {6, 7}};
        for (int i = 0; i < asignaciones.size(); i++) {
            Asignacion asignacion = asignaciones.get(i);
            for (int indice : indices[i % indices.length]) {
                Actividad actividad = actividades.get(indice);
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

    private void crearRespuestas(
            Intento camila1,
            Intento camila2,
            Intento mateo1,
            BancoPreguntas banco,
            List<Actividad> actividades
    ) {
        responder(camila1, banco, actividades.get(2), 3, 0);
        responder(camila1, banco, actividades.get(2), 4, 0);
        responder(camila1, banco, actividades.get(2), 5, 1);
        responder(camila2, banco, actividades.get(2), 3, 1);
        responder(camila2, banco, actividades.get(2), 4, 1);
        responder(camila2, banco, actividades.get(2), 5, 0);
        responder(mateo1, banco, actividades.get(5), 3, 2);
        responder(mateo1, banco, actividades.get(5), 4, 0);
        responder(mateo1, banco, actividades.get(5), 5, 0);
    }

    private void responder(Intento intento, BancoPreguntas banco, Actividad actividad,
                           int orden, int indiceAlternativa) {
        PreguntaCreada creada = banco.buscar(actividad.getId(), orden);
        Alternativa alternativa = creada.alternativas().get(indiceAlternativa);
        respuestaEstudianteRepository.save(RespuestaEstudiante.builder()
                .intento(intento)
                .pregunta(creada.pregunta())
                .alternativaId(alternativa.getId())
                .esCorrecta(alternativa.getEsCorrecta())
                .build());
    }

    private void crearInforme(Usuarios usuarios, Unidad unidad, List<ObjetivoAprendizaje> oas) {
        InformePedagogico informe = informePedagogicoRepository.save(InformePedagogico.builder()
                .estudiante(usuarios.camila())
                .unidad(unidad)
                .docente(usuarios.docente())
                .fechaGeneracion(Instant.parse("2026-05-08T09:00:00Z"))
                .estado(EstadoInforme.PENDIENTE_VALIDACION)
                .fechaValidacion(null)
                .build());

        detalleInformeOaRepository.save(detalle(informe, oas.get(1), 3, 2, NivelOa.REFUERZO));
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

    private record Usuarios(Usuario utp, Usuario docente, Usuario camila,
                            Usuario mateo, Usuario javiera, Usuario benjamin) {
    }

    private record PreguntaCreada(Pregunta pregunta, List<Alternativa> alternativas) {
    }

    private record BancoPreguntas(List<PreguntaCreada> preguntas) {
        PreguntaCreada buscar(Long actividadId, int orden) {
            return preguntas.stream()
                    .filter(p -> p.pregunta().getActividad().getId().equals(actividadId)
                            && p.pregunta().getOrden() == orden)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No existe la pregunta " + actividadId + "/" + orden
                    ));
        }
    }
}
