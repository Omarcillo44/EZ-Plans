package com.ezsoftware.ezplans.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @ColumnDefault("nextval('pagos_id_pago_seq')")
    @Column(name = "id_pago", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_deuda", nullable = false)
    private Deuda idDeuda;

    @Column(name = "monto_pago", nullable = false)
    private BigDecimal montoPago;

    @Column(name = "forma_pago")
    private Boolean formaPago;

    @Column(name = "comprobante_pago")
    private String comprobantePago;

}