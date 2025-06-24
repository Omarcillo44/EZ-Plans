// DatosVistaPago.kt
package com.ezsoftware.ezplans.models

data class DatosVistaPago(
    val idPago: Int,
    val nombreDeudor: String,
    val nombreAcreedor: String,
    val tituloPlan: String,
    val tituloActividad: String,
    val montoRestanteDeuda: String,
    val montoPago: String,
    val comprobantePago: String?
)