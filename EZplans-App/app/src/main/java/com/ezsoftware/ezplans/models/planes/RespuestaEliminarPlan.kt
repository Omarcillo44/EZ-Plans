// RespuestaEliminarPlan.kt
package com.ezsoftware.ezplans.models.planes

import android.util.Log

data class RespuestaEliminarPlan(
    val mensaje: String,
    val esExitoso: Boolean
) {
    companion object {
        fun fromString(response: String): RespuestaEliminarPlan {
            Log.d("RespuestaEliminarPlan", "=== PROCESANDO RESPUESTA DEL SERVIDOR ===")
            Log.d("RespuestaEliminarPlan", "Respuesta cruda: '$response'")
            
            val esExitoso = response.contains("eliminado exitosamente")
            Log.d("RespuestaEliminarPlan", "Operación ${if (esExitoso) "exitosa" else "fallida"}")
            
            return RespuestaEliminarPlan(
                mensaje = response,
                esExitoso = esExitoso
            ).also {
                Log.d("RespuestaEliminarPlan", "Objeto creado: $it")
            }
        }
    }
}