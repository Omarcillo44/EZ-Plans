package com.ezsoftware.ezplans.models.NuevoPlan

import com.fasterxml.jackson.annotation.JsonProperty

data class DatosNuevoPlan(
    @JsonProperty("titulo") val titulo: String,
    @JsonProperty("fechaPlan") val fechaPlan: String, // Formato: YYYY-MM-DD
    @JsonProperty("detallesPlan") val detallesPlan: String,
    @JsonProperty("miembros") val miembros: List<DatosMiembrosNuevoPlan>
)