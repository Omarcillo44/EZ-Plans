package com.ezsoftware.ezplans.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "miembros_actividad")
public class MiembrosActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // o la estrategia que uses en tu BD
    @Column(name = "id_miembrosactividad", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad idActividad;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "aportacion", nullable = false)
    private BigDecimal aportacion;

}