package com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan;

public record DatosDeudasPorPlan(
    Integer idDeuda,
    Integer idDeudor,
    String nombreDeudor,
    Integer idAcreedor,
    String nombreAcreedor,
    String tituloActividad,
    String montoDeuda
) {}
