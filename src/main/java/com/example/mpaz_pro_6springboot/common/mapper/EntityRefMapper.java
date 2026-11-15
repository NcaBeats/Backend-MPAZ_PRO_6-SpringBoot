package com.example.mpaz_pro_6springboot.common.mapper;

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
import com.example.mpaz_pro_6springboot.docencia.model.ActividadCurso;
import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;
import com.example.mpaz_pro_6springboot.identidad.model.Nivel;
import com.example.mpaz_pro_6springboot.identidad.model.Rol;
import com.example.mpaz_pro_6springboot.identidad.model.Seccion;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;
import org.springframework.stereotype.Component;

/**
 * Construye referencias JPA a partir de un identificador.
 *
 * <p>Los mappers de entrada usan estas referencias para que los servicios
 * existentes puedan resolver la entidad real con {@code findById}. La
 * referencia solo lleva el id; no se debe persistir directamente.</p>
 */
@Component
public final class EntityRefMapper {

    public Actividad actividadDesdeId(Long id) {
        return id == null ? null : Actividad.builder().id(id).build();
    }

    public ActividadCurso actividadCursoDesdeId(Long id) {
        return id == null ? null : ActividadCurso.builder().id(id).build();
    }

    public Alternativa alternativaDesdeId(Long id) {
        return id == null ? null : Alternativa.builder().id(id).build();
    }

    public Asignatura asignaturaDesdeId(Long id) {
        return id == null ? null : Asignatura.builder().id(id).build();
    }

    public Contenido contenidoDesdeId(Long id) {
        return id == null ? null : Contenido.builder().id(id).build();
    }

    public Curso cursoDesdeId(Long id) {
        return id == null ? null : Curso.builder().id(id).build();
    }

    public EstadoAutorizacion estadoAutorizacionDesdeId(Long id) {
        return id == null ? null : EstadoAutorizacion.builder().id(id).build();
    }

    public InformePedagogico informePedagogicoDesdeId(Long id) {
        return id == null ? null : InformePedagogico.builder().id(id).build();
    }

    public Intento intentoDesdeId(Long id) {
        return id == null ? null : Intento.builder().id(id).build();
    }

    public Nivel nivelDesdeId(Long id) {
        return id == null ? null : Nivel.builder().id(id).build();
    }

    public ObjetivoAprendizaje objetivoAprendizajeDesdeId(Long id) {
        return id == null ? null : ObjetivoAprendizaje.builder().id(id).build();
    }

    public Pregunta preguntaDesdeId(Long id) {
        return id == null ? null : Pregunta.builder().id(id).build();
    }

    public Recurso recursoDesdeId(Long id) {
        return id == null ? null : Recurso.builder().id(id).build();
    }

    public Rol rolDesdeId(Long id) {
        return id == null ? null : Rol.builder().id(id).build();
    }

    public Seccion seccionDesdeId(Long id) {
        return id == null ? null : Seccion.builder().id(id).build();
    }

    public TipoActividad tipoActividadDesdeId(Long id) {
        return id == null ? null : TipoActividad.builder().id(id).build();
    }

    public TipoRecurso tipoRecursoDesdeId(Long id) {
        return id == null ? null : TipoRecurso.builder().id(id).build();
    }

    public Unidad unidadDesdeId(Long id) {
        return id == null ? null : Unidad.builder().id(id).build();
    }

    public Usuario usuarioDesdeId(Long id) {
        return id == null ? null : Usuario.builder().id(id).build();
    }
}
