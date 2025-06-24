package com.ezsoftware.ezplans.models.dashboard

enum class FiltroEstadoPlan(val label: String, val soloCompletos: Boolean?) {
    TODOS("Todos", null),
    COMPLETOS("Completos", true),
    PENDIENTES("Pendientes", false);

    override fun toString(): String = label
}
