package com.ezsoftware.ezplans.models.dashboard

import com.fasterxml.jackson.annotation.JsonProperty

data class datosDashboard(
    @JsonProperty("resumenPlanes")
    val datosResumen: datosResumen,

    @JsonProperty("planesUsuario")
    val datosPlanes: List<datosPlan>
)
