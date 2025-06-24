package com.ezsoftware.ezplans.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "planes")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan", nullable = false)
    private Integer id;

    @Column(name = "titulo_plan", nullable = false, length = Integer.MAX_VALUE)
    private String tituloPlan;

    @Column(name = "fecha_plan", nullable = false)
    private LocalDate fechaPlan;

    @Column(name = "gasto_plan")
    private BigDecimal gastoPlan;

    @Column(name = "detalles_plan", length = Integer.MAX_VALUE)
    private String detallesPlan;

    @Column(name = "estado_plan")
    private Boolean estadoPlan;

    public Plan(String titulo, LocalDate fechaPlan, String detallesPlan) {
        this.tituloPlan = titulo;
        this.fechaPlan = fechaPlan;
        this.detallesPlan = detallesPlan;
        this.estadoPlan = false; // o true si deseas activarlo por defecto
        this.gastoPlan = BigDecimal.ZERO; // inicia en cero
    }

    public Plan() {

    }
}