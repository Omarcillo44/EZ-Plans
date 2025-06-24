// DatosNuevaActividad.kt
package com.ezsoftware.ezplans.models.NuevaActividad

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class DatosNuevaActividad(
    @JsonProperty("idPlan") val idPlan: Int,
    @JsonProperty("titulo") val titulo: String,
    @JsonProperty("montoTotal") val montoTotal: BigDecimal,
    @JsonProperty("detalles") val detalles: String,
    @JsonProperty("miembros") val miembros: List<DatosMiembrosNuevaActividad>
)