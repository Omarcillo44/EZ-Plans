package com.ezsoftware.ezplans.Model.DTO.Pagos;

import java.math.BigDecimal;

public record DatosNuevoPago(
    Integer idDeuda,
    BigDecimal montoPago,
    Boolean formaPago,
    String comprobantePago
) {}
