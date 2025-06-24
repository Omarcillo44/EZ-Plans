// DatosMiembrosNuevaActividad.kt
package com.ezsoftware.ezplans.models.NuevaActividad

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class DatosMiembrosNuevaActividad(
    @JsonProperty("idUsuario") val idUsuario: Int,
    @JsonProperty("aportacion") val aportacion: BigDecimal,
    @JsonProperty("idAcreedorDeuda") val idAcreedorDeuda: Int?,
    @JsonProperty("montoDeuda") val montoDeuda: BigDecimal,
    @JsonProperty("montoCorrespondiente") val montoCorrespondiente: BigDecimal
)