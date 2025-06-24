package com.ezsoftware.ezplans.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "deudas")
public class Deuda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deuda", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_miembrosactividad", nullable = false)
    private com.ezsoftware.ezplans.Model.Entity.MiembrosActividad idMiembrosactividad;

    @Column(name = "id_acreedor", nullable = false)
    private Integer idAcreedor;

    @Column(name = "monto_deuda", nullable = false)
    private BigDecimal montoDeuda;

}