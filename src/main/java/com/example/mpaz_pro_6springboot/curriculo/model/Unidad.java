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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Unidad de una asignatura. Agrupa contenidos y ofrece objetivos de aprendizaje.
 */
@Entity
@Table(name = "unidad")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"asignatura", "estado"})
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unidad_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoAutorizacion estado;
}
