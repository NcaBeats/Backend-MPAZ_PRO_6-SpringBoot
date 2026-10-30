package com.example.mpaz_pro_6springboot.curriculo.model;

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
 * Relacion N:M entre objetivo de aprendizaje y contenido, acotada a una unidad.
 *
 * <p>Las claves foraneas compuestas de PostgreSQL garantizan que el contenido
 * y el OA pertenezcan a la unidad indicada. La entidad expone las tres
 * relaciones por separado para poder operar con JPA; la coherencia la valida
 * la base de datos.</p>
 */
@Entity
@Table(
        name = "objetivo_aprendizaje_contenido",
        uniqueConstraints = @UniqueConstraint(
                name = "oa_cont_un",
                columnNames = {"contenido_id", "objetivo_aprendizaje_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"objetivoAprendizaje", "contenido", "unidad"})
public class ObjetivoAprendizajeContenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oa_contenido_id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objetivo_aprendizaje_id", nullable = false)
    private ObjetivoAprendizaje objetivoAprendizaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contenido_id", nullable = false)
    private Contenido contenido;

    /**
     * Debe coincidir con {@code contenido.unidad} y con una unidad en la que
     * el OA este ofrecido. La FK compuesta lo exige en la base de datos.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;
}
