package com.example.mpaz_pro_6springboot.curriculo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objetivo de aprendizaje. Puede impartirse en varias unidades; la relacion
 * se representa en {@link ObjetivoAprendizajeUnidad}.
 */
@Entity
@Table(name = "objetivo_aprendizaje")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ObjetivoAprendizaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "objetivo_aprendizaje_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "descripcion", nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(name = "codigo", nullable = false, length = 25)
    private String codigo;
}
