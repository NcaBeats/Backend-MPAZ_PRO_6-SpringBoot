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
 * Nivel educativo (por ejemplo: Sexto Basico).
 */
@Entity
@Table(
        name = "nivel",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_nivel_nombre",
                columnNames = "nombre"
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Nivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nivel_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nombre", nullable = false, length = 25)
    private String nombre;
}
