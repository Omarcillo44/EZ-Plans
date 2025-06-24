package com.ezsoftware.ezplans.Model.DTO.NuevaActividad;

import java.math.BigDecimal;

public record DatosMiembrosNuevaActividad(
        Integer idUsuario,
        BigDecimal aportacion,
        Integer idAcreedorDeuda,
        BigDecimal montoDeuda,
        BigDecimal montoCorrespondiente
) {}