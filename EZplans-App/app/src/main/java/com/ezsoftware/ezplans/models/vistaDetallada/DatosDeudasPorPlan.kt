package com.ezsoftware.ezplans.models.vistaDetallada

data class DatosDeudasPorPlan(
    val idDeuda: Int,
    val idDeudor: Int,
    val nombreDeudor: String,
    val idAcreedor: Int,
    val nombreAcreedor: String,
    val tituloActividad: String,
    val montoDeuda: String
)