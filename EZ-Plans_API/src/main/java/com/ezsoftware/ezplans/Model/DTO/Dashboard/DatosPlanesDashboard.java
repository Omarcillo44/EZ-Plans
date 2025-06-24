package com.ezsoftware.ezplans.Model.DTO.Dashboard;

public record DatosPlanesDashboard(
        Integer idPlan,
        String titulo,
        String fecha,
        String gastoTotal,
        String numeroDeMiembros,
        String rolDelUsuario,
        String estadoDelPlan
) { }
