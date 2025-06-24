package com.ezsoftware.ezplans.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "actividades")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan idPlan;

    @Column(name = "titulo_actividad", nullable = false, length = Integer.MAX_VALUE)
    private String tituloActividad;

    @Column(name = "gasto_actividad")
    private BigDecimal gastoActividad;

    @Column(name = "detalles_actividad", length = Integer.MAX_VALUE)
    private String detallesActividad;

    @Column(name = "estado_actividad")
    private Boolean estadoActividad;

}