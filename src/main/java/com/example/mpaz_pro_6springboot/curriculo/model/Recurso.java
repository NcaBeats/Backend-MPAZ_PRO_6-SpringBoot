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
 * Recurso pedagogico asociado a un contenido.
 */
@Entity
@Table(name = "recurso")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"tipoRecurso", "contenido"})
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurso_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_recurso_id", nullable = false)
    private TipoRecurso tipoRecurso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contenido_id", nullable = false)
    private Contenido contenido;
}
