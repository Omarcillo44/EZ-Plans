package com.ezsoftware.ezplans.Repository;

import com.ezsoftware.ezplans.Model.DTO.Dashboard.DatosPlanesDashboard;
import com.ezsoftware.ezplans.Model.DTO.Dashboard.DatosResumenDashboard;
import com.ezsoftware.ezplans.Model.DTO.EditarPlan.DatosVistaEditarPlan;
import com.ezsoftware.ezplans.Model.DTO.NuevaActividad.Contactos.DatosUsuarioEnPlan;
import com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan.DatosActividadPlan;
import com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan.DatosResumenMiembrosPlan;
import com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan.DatosDeudasPorPlan;
import com.ezsoftware.ezplans.Model.DTO.VistaDetalladaPlan.DatosResumenPlan;
import com.ezsoftware.ezplans.Model.Entity.Plan;
import com.ezsoftware.ezplans.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanesRepository extends JpaRepository<Plan, Integer> {

    @Query(value = "SELECT * FROM obtener_planes_usuario(:idUsuario, :soloCompletos, :esAdmin)", nativeQuery = true)
    List<DatosPlanesDashboard> obtenerPlanesPorUsuario(
            @Param("idUsuario") Integer idUsuario,
            @Param("soloCompletos") Boolean soloCompletos,
            @Param("esAdmin") Boolean esAdmin
    );

    @Query(value = "SELECT * FROM obtener_resumen_usuario(:idUsuario)", nativeQuery = true)
    DatosResumenDashboard obtenResumenDashboardPorUsuario(
            @Param("idUsuario") Integer idUsuario
    );

    @Query(value = "SELECT * FROM obtener_resumen_plan(:idPlan)", nativeQuery = true)
    DatosResumenPlan obtenResumenPorPlan(@Param("idPlan") Integer idPlan);

    @Query(value = "SELECT * FROM obtener_actividades_por_plan(:idPlan)", nativeQuery = true)
    List<DatosActividadPlan> obtenResumenActividadesPorPlan(@Param("idPlan") Integer idPlan);

    @Query(value = "SELECT * FROM obtener_resumen_miembros_plan(:idPlan)", nativeQuery = true)
    List<DatosResumenMiembrosPlan> obtenResumenPorMiembrosDelPlan(@Param("idPlan") Integer idPlan);

    @Modifying
    @Transactional
    @Query(value = "CALL eliminar_plan_completo(:idPlan)", nativeQuery = true)
    void eliminarPlanCompleto(@Param("idPlan") Integer idPlan);

    @Modifying
    @Transactional
    @Query(value = "CALL actualizar_plan(:idPlan, :titulo, :detalles, :fecha)", nativeQuery = true)
    void actualizarPlan(@Param("idPlan") Integer idPlan,
                        @Param("titulo") String titulo,
                        @Param("detalles") String detalles,
                        @Param("fecha") LocalDate fecha);

    @Query(value = "SELECT * FROM obtener_datos_editar_plan(:idPlan)", nativeQuery = true)
    DatosVistaEditarPlan obtenerDatosVistaEditar(@Param("idPlan") Integer idPlan);
}
