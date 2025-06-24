package com.ezsoftware.ezplans.Repository;

import com.ezsoftware.ezplans.Model.DTO.NuevaActividad.Contactos.DatosUsuarioEnPlan;
import com.ezsoftware.ezplans.Model.Entity.MiembrosPlan;
import com.ezsoftware.ezplans.Model.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MiembrosPlanRepository extends JpaRepository<MiembrosPlan, Integer> {
        MiembrosPlan findByIdPlan(Plan idPlan);

        @Query("SELECT mp.idUsuario.id FROM MiembrosPlan mp WHERE mp.idPlan.id = :idPlan")
        List<Integer> findUsuariosPorPlan(@Param("idPlan") Integer idPlan);

        @Query(value = "SELECT * FROM obten_miembros_plan(:idPlan)", nativeQuery = true)
        List<DatosUsuarioEnPlan> obtenerMiembrosPorPlan(@Param("idPlan") Integer idPlan);

}
