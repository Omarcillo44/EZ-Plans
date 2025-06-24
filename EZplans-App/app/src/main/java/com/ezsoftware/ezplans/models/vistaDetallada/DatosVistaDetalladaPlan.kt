package com.ezsoftware.ezplans.models.vistaDetallada

import com.fasterxml.jackson.annotation.JsonProperty

// Resumen principal del plan
data class DatosVistaDetalladaPlan(
    @JsonProperty("resumenPlan") val resumenPlan: DatosResumenPlan,
    @JsonProperty("resumenActividadesPlan") val actividades: List<DatosActividadPlan>,
    @JsonProperty("resumenMiembrosPlan") val miembros: List<DatosResumenMiembrosPlan>,
    @JsonProperty("resumenDeudasPLan") val deudas: List<DatosDeudasPorPlan>
)