package com.ezsoftware.ezplans.models.vistaDetallada
data class DatosActividadPlan(
    val idActividad: Int,
    val tituloActividad: String,
    val montoActividad: String,
    val numeroDeudasPendientesActividad: String,
    val estadoActividad: String
)