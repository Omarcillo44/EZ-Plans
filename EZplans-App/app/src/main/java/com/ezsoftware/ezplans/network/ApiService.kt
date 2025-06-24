package com.ezsoftware.ezplans.network

import com.ezsoftware.ezplans.models.DatosVistaPago
import com.ezsoftware.ezplans.models.planes.DatosEditarPlan
import com.ezsoftware.ezplans.models.planes.DatosVistaEditarPlan
import com.ezsoftware.ezplans.models.NuevaActividad.DatosNuevaActividad
import com.ezsoftware.ezplans.models.Pagos.DatosNuevoPago
import com.ezsoftware.ezplans.models.NuevoPlan.DatosNuevoPlan
import com.ezsoftware.ezplans.models.NuevaActividad.Miembros.DatosUsuarioEnPlan
import com.ezsoftware.ezplans.models.vistaDetallada.DatosVistaDetalladaPlan
import com.ezsoftware.ezplans.models.acceso.AccesoRequest
import com.ezsoftware.ezplans.models.acceso.AccesoResponse
import com.ezsoftware.ezplans.models.dashboard.datosDashboard
import com.ezsoftware.ezplans.models.user.DatosUsuarioResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @POST("/login")
    suspend fun login(@Body bodyRequest: AccesoRequest): Response<AccesoResponse>

    @GET("/usuario/datos")
    suspend fun obtenerDatosUsuario(
        @Query("celularUsuario") celularUsuario: String,
        @Header("Authorization") token: String
    ): Response<DatosUsuarioResponse>

    @GET("/dashboard")
    suspend fun obtenerDatosDashboard(
        @Query("idUsuario") idUsuario: Int,
        @Query("soloCompletos") soloCompletos: Boolean?,
        @Query("esAdmin") esAdmin: Boolean?,
        @Header("Authorization") token: String
    ): Response<datosDashboard>

    @GET("/planes/vista_detallada")
    suspend fun obtenerVistaDetalladaPlan(
        @Query("idPlan") idPlan: Int,
        @Header("Authorization") token: String
    ): Response<DatosVistaDetalladaPlan>

    @POST("planes/nuevo_plan")
    suspend fun crearNuevoPlan(
        @Body datosPlan: DatosNuevoPlan,
        @Header("Authorization") token: String
    ): Response<ResponseBody> // Usar ResponseBody para respuesta cruda

    @GET("/planes/miembros")
    suspend fun obtenerMiembrosPlan(
        @Query("idPlan") idPlan: Int,
        @Header("Authorization") token: String
    ): Response<List<DatosUsuarioEnPlan>>

    @POST("/pagos/nuevo_pago")
    suspend fun registrarNuevoPago(
        @Body datosPago: DatosNuevoPago,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @POST("/actividades/nueva_actividad")
    suspend fun crearNuevaActividad(
        @Body datosActividad: DatosNuevaActividad,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @DELETE("/planes/eliminar")
    suspend fun eliminarPlan(
        @Query("idPlan") idPlan: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    // Agregar este método en la interfaz ApiService.kt
    @GET("/planes/vistaEditarPlan")
    suspend fun obtenerDatosEditarPlan(
        @Query("idPlan") idPlan: Int,
        @Header("Authorization") token: String
    ): Response<DatosVistaEditarPlan>

    // Agregar en ApiService.kt
    @PUT("/planes/editar")
    suspend fun actualizarPlan(
        @Body datosPlan: DatosEditarPlan,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/pagos/ver")
    suspend fun obtenerPagosUsuario(
        @Query("idUsuario") idUsuario: Int,
        @Header("Authorization") token: String
    ): Response<List<DatosVistaPago>>
}