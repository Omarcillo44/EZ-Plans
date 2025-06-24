package com.ezsoftware.ezplans.Model.DTO.NuevoPlan;

import java.time.LocalDate;
import java.util.List;

public record DatosNuevoPlan(
    String titulo,
    LocalDate fechaPlan,
    String detallesPlan,
    List<DatosMiembrosNuevoPlan> miembros
) {}


