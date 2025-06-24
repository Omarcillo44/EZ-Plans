// RespuestaEditarPlan.kt
package com.ezsoftware.ezplans.models.planes

import android.util.Log

data class RespuestaEditarPlan(
    val mensaje: String,
    val esExitoso: Boolean
) {
    companion object {
        fun fromString(response: String): RespuestaEditarPlan {
            Log.d("RespuestaEditarPlan", "Respuesta del servidor: $response")
            return RespuestaEditarPlan(
                mensaje = response,
                esExitoso = response.contains("actualizado correctamente")
            )
        }
    }
}