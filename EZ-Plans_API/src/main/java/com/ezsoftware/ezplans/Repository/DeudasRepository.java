package com.ezsoftware.ezplans.Repository;

import com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan.DatosDeudasPorPlan;
import com.ezsoftware.ezplans.Model.Entity.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DeudasRepository extends JpaRepository<Deuda,Integer> {
    @Query(value = "SELECT * FROM obtener_deudas_por_plan(:idPlan)", nativeQuery = true)
    List<DatosDeudasPorPlan> obtenResumenDeudasPorPlan(@Param("idPlan") Integer idPlan);
}
