package com.example.mpaz_pro_6springboot.docencia.model;

import java.time.Instant;

import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;
import com.example.mpaz_pro_6springboot.identidad.model.Curso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Asignacion de una actividad a un curso, con su configuracion propia
 * (ventana de disponibilidad, habilitacion, visibilidad y limite de intentos).
 *
 * <p>La actividad es la plantilla reutilizable; esta fila es su publicacion en
 * un curso concreto. La misma actividad puede estar asignada a varios cursos
 * con configuracion independiente.</p>
 */
@Entity
@Table(
        name = "actividad_curso",
        uniqueConstraints = @UniqueConstraint(
                name = "actividad_curso__un",
                columnNames = {"actividad_id", "curso_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"actividad", "curso"})
public class ActividadCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actividad_curso_id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    /**
     * Inicio de la ventana de disponibilidad. {@code null} = sin apertura
     * programada.
     */
    @Column(name = "fecha_inicio")
    private Instant fechaInicio;

    /**
     * Fin de la ventana de disponibilidad. {@code null} = sin cierre.
     */
    @Column(name = "fecha_fin")
    private Instant fechaFin;

    @Column(name = "habilitada", nullable = false)
    private boolean habilitada;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    /**
     * Nullable: {@code null} significa sin limite de intentos para este curso.
     */
    @Column(name = "max_intentos")
    private Integer maxIntentos;
}
