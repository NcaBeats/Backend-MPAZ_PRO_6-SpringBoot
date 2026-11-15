package com.example.mpaz_pro_6springboot.curriculo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Estado de autorizacion de contenidos, unidades y actividades.
 */
@Entity
@Table(
        name = "estado_autorizacion",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_ea_nombre",
                columnNames = "nombre"
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EstadoAutorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estado_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
}
