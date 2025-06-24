package com.ezsoftware.ezplans.Model.DTO.EditarPlan;

import java.sql.Date;
import java.time.LocalDate;

public record DatosVistaEditarPlan(
        Integer idPlan,
        String titulo,
        Date fecha,
        String detalles,
        Integer idAdministrador
) {}
