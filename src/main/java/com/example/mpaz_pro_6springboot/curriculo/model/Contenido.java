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
 * Contenido pedagogico. Pertenece a una unica unidad y esa unidad es inmutable
 * a nivel de base de datos (trigger {@code trg_contenido_unidad_inmutable}).
 */
@Entity
@Table(
        name = "contenido",
        uniqueConstraints = @UniqueConstraint(
                name = "contenido__un",
                columnNames = {"contenido_id", "unidad_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"estado", "unidad"})
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contenido_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoAutorizacion estado;

    /**
     * Unidad propietaria del contenido. Sin setter: la columna no se puede
     * actualizar una vez creada la fila.
     */
    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;
}
