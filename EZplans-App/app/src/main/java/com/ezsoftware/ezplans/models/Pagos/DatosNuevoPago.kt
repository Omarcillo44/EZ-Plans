package com.ezsoftware.ezplans.models.Pagos

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class DatosNuevoPago(
    @JsonProperty("idDeuda") val idDeuda: Int,
    @JsonProperty("montoPago") val montoPago: BigDecimal,
    @JsonProperty("formaPago") val formaPago: Boolean,
    @JsonProperty("comprobantePago") val comprobantePago: String?
)