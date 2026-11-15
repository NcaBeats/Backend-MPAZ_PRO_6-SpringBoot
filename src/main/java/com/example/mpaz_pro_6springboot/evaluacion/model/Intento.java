package com.example.mpaz_pro_6springboot.evaluacion.model;

import java.time.Instant;

import com.example.mpaz_pro_6springboot.docencia.model.ActividadCurso;
import com.example.mpaz_pro_6springboot.identidad.model.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Intento de una actividad asignada a un curso, realizado por un usuario.
 *
 * <p>Se registra contra {@code actividad_curso}: asi queda acotado al curso y
 * a la configuracion (ventana y limite de intentos) vigente.</p>
 */
@Entity
@Table(name = "intento")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"usuario", "actividadCurso"})
public class Intento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intento_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "fecha_fin")
    private Instant fechaFin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_curso_id", nullable = false)
    private ActividadCurso actividadCurso;
}
