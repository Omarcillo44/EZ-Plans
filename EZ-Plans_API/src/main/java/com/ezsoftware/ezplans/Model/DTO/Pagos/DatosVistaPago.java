package com.ezsoftware.ezplans.Model.DTO.Pagos;

import java.math.BigDecimal;

public record DatosVistaPago(
        Integer idPago,
        String nombreDeudor,
        String nombreAcreedor,
        String tituloPlan,
        String tituloActividad,
        String montoRestanteDeuda,
        String montoPago,
        String comprobantePago
) {
}

