package com.ezsoftware.ezplans.models.NuevaActividad.Miembros

import com.fasterxml.jackson.annotation.JsonProperty

data class DatosUsuarioEnPlan(
    @JsonProperty("idUsuario") val idUsuario: Int,
    @JsonProperty("celularUsuario") val celularUsuario: String,
    @JsonProperty("nombreUsuario") val nombreUsuario: String,
    @JsonProperty("apellidosUsuario") val apellidosUsuario: String
)