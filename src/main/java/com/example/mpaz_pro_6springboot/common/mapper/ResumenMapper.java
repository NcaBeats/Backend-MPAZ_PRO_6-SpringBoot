package com.example.mpaz_pro_6springboot.common.mapper;

import com.example.mpaz_pro_6springboot.curriculo.dto.response.ActividadResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AlternativaResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.AsignaturaResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ContenidoResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.EstadoAutorizacionResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.ObjetivoAprendizajeResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.PreguntaResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.RecursoResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.TipoActividadResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.TipoRecursoResumen;
import com.example.mpaz_pro_6springboot.curriculo.dto.response.UnidadResumen;
import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;
import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;
import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;
import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;
import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoActividad;
import com.example.mpaz_pro_6springboot.curriculo.model.TipoRecurso;
import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;
import com.example.mpaz_pro_6springboot.evaluacion.dto.response.IntentoResumen;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import com.example.mpaz_pro_6springboot.identidad.dto.response.CursoResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.NivelResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.RolResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.SeccionResumen;
import com.example.mpaz_pro_6springboot.identidad.dto.response.UsuarioResumen;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.model.Nivel;
import com.example.mpaz_pro_6springboot.identidad.model.Rol;
import com.example.mpaz_pro_6springboot.identidad.model.Seccion;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.informe.dto.response.InformePedagogicoResumen;
import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Convierte entidades en los resumenes anidados que usan los DTOs de respuesta.
 *
 * <p>Se centraliza aqui para que todos los mappers de feature compartan las
 * mismas representaciones y no se dupliquen conversiones.</p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResumenMapper {

    RolResumen toResumen(Rol rol);

    NivelResumen toResumen(Nivel nivel);

    SeccionResumen toResumen(Seccion seccion);

    CursoResumen toResumen(Curso curso);

    @Mapping(target = "nombreCompleto", expression = "java(construirNombreCompleto(usuario))")
    UsuarioResumen toResumen(Usuario usuario);

    AsignaturaResumen toResumen(Asignatura asignatura);

    EstadoAutorizacionResumen toResumen(EstadoAutorizacion estadoAutorizacion);

    TipoActividadResumen toResumen(TipoActividad tipoActividad);

    TipoRecursoResumen toResumen(TipoRecurso tipoRecurso);

    UnidadResumen toResumen(Unidad unidad);

    ObjetivoAprendizajeResumen toResumen(ObjetivoAprendizaje objetivoAprendizaje);

    ContenidoResumen toResumen(Contenido contenido);

    RecursoResumen toResumen(Recurso recurso);

    ActividadResumen toResumen(Actividad actividad);

    PreguntaResumen toResumen(Pregunta pregunta);

    AlternativaResumen toResumen(Alternativa alternativa);

    IntentoResumen toResumen(Intento intento);

    InformePedagogicoResumen toResumen(InformePedagogico informePedagogico);

    /**
     * Arma el nombre completo con las partes disponibles, sin dejar dobles espacios.
     */
    default String construirNombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        StringBuilder nombre = new StringBuilder();
        agregarParte(nombre, usuario.getPnombre());
        agregarParte(nombre, usuario.getSnombre());
        agregarParte(nombre, usuario.getApaterno());
        agregarParte(nombre, usuario.getAmaterno());
        return nombre.toString();
    }

    private void agregarParte(StringBuilder nombre, String parte) {
        if (parte == null || parte.isBlank()) {
            return;
        }
        if (!nombre.isEmpty()) {
            nombre.append(' ');
        }
        nombre.append(parte.trim());
    }
}
