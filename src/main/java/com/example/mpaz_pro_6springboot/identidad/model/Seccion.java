package com.example.mpaz_pro_6springboot.identidad.model;

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
 * Seccion o letra de un curso (por ejemplo: A, B, C).
 */
@Entity
@Table(
        name = "seccion",
        uniqueConstraints = @UniqueConstraint(
                name = "seccion__un",
                columnNames = "letra"
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seccion_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "letra", nullable = false, length = 1)
    private String letra;
}
