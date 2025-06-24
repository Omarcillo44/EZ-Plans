package com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan;

public record DatosActividadPlan(
    int idActividad,
    String tituloActividad,
    String montoActividad,
    String numeroDeudasPendientesActividad,
    String estadoActividad
) {}
