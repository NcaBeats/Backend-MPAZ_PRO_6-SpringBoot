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
 * Pregunta reutilizable. Se asocia a actividades mediante
 * {@link PreguntaActividad} y a alternativas mediante {@link AlternativaPregunta}.
 */
@Entity
@Table(name = "pregunta")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "recurso")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pregunta_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "enunciado", nullable = false, columnDefinition = "text")
    private String enunciado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;
}
