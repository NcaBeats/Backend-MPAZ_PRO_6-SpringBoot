package com.example.mpaz_pro_6springboot.evaluacion.model;

import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;
import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;

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
 * Respuesta seleccionada por un estudiante en un intento.
 *
 * <p>PostgreSQL exige, mediante la FK compuesta
 * {@code (pregunta_id, alternativa_id) -> alternativa_pregunta}, que la
 * alternativa pertenezca a la pregunta. La entidad expone las relaciones
 * directas para operar con JPA; la coherencia la garantiza la base de datos.</p>
 */
@Entity
@Table(
        name = "respuesta",
        uniqueConstraints = @UniqueConstraint(
                name = "respuesta__unv1",
                columnNames = {"intento_id", "pregunta_id", "alternativa_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"intento", "pregunta", "alternativa"})
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "respuesta_id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intento_id", nullable = false)
    private Intento intento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alternativa_id", nullable = false)
    private Alternativa alternativa;
}
