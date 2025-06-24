package com.ezsoftware.ezplans.Model.DTO.EditarPlan;

import java.time.LocalDate;

public record DatosEditarPlan(
        Integer idPlan,
        String titulo,
        String detalles,
        LocalDate fecha
) {}
