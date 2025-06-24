package com.ezsoftware.ezplans.Model.DTO.Dashboard;

import java.util.List;

public record DatosDashboard(
        DatosResumenDashboard resumenPlanes,
        List<DatosPlanesDashboard> planesUsuario
) {
}
