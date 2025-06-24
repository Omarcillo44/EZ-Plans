package com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan;

import java.util.List;

public record DatosVistaDetalladaPlan(
        DatosResumenPlan resumenPlan,
        List<DatosActividadPlan> resumenActividadesPlan,
        List<DatosResumenMiembrosPlan> resumenMiembrosPlan,
        List<DatosDeudasPorPlan> resumenDeudasPLan
) {
}
