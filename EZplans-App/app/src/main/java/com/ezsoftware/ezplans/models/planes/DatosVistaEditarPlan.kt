// DatosVistaEditarPlan.kt
package com.ezsoftware.ezplans.models.planes

data class DatosVistaEditarPlan(
    val idPlan: Int,
    val titulo: String,
    val fecha: String,
    val detalles: String,
    val idAdministrador: Int
)