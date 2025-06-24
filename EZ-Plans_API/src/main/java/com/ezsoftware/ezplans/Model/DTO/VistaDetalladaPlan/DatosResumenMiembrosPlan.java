package com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan;

public record DatosResumenMiembrosPlan(
    Integer idMiembro,
    String nombreMiembro,
    String celularMiembro,
    String montoDeuda,
    String montoAportacion,
    Boolean tieneDeuda
) {}
