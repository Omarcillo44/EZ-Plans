package com.ezsoftware.ezplans.Model.DTO.NuevaActividad;

import java.util.List;

public record DatosNuevaActividad(
        Integer idPlan,
        String titulo,
        String detalles,
        List<DatosMiembrosNuevaActividad> miembros
) {}
