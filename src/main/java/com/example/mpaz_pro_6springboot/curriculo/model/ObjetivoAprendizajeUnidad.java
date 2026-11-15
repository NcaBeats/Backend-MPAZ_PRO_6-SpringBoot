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
 * Relacion N:M entre objetivo de aprendizaje y unidad. Permite que un OA se
 * imparta en varias unidades (curriculum en espiral).
 */
@Entity
@Table(
        name = "objetivo_aprendizaje_unidad",
        uniqueConstraints = @UniqueConstraint(
                name = "oa_unidad__un",
                columnNames = {"objetivo_aprendizaje_id", "unidad_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"objetivoAprendizaje", "unidad"})
public class ObjetivoAprendizajeUnidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oa_unidad_id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objetivo_aprendizaje_id", nullable = false)
    private ObjetivoAprendizaje objetivoAprendizaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;
}
