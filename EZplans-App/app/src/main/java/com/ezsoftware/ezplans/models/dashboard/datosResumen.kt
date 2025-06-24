package com.ezsoftware.ezplans.models.dashboard

data class datosResumen(
    val planesAdministrados: Int,
    val planesMiembro: Int,
    val deudasPendientes: String,
    val deudasPorCobrar: String
)
