package com.ezsoftware.ezplans.models.vistaDetallada
data class DatosResumenPlan(
    val idPlan: Int,
    val tituloPlan: String,
    val fechaPlan: String,
    val estadoPlan: String,
    val avancePlan: Double,
    val gastoPlan: String,
    val cantidadMiembrosPlan: String,
    val actividadesCompletadasPlan: String,
    val cantidadDeudasPendientesPlan: Int,
    val idAdministrador: Int
)