package com.ezsoftware.ezplans.models.dashboard

data class datosPlan(
    val idPlan: Int,
    val titulo: String,
    val fecha: String,
    val gastoTotal: String,
    val numeroDeMiembros: String,
    val rolDelUsuario: String,
    val estadoDelPlan: String
)
