// RespuestaNuevaActividad.kt
package com.ezsoftware.ezplans.models.NuevaActividad

import android.util.Log

data class RespuestaNuevaActividad(
    val mensaje: String,
    val idActividad: Int? = null
) {
    companion object {
        fun fromString(response: String): RespuestaNuevaActividad {
            Log.d("RespuestaNuevaActividad", "=== PROCESANDO RESPUESTA DEL SERVIDOR ===")
            Log.d("RespuestaNuevaActividad", "Respuesta cruda: '$response'")
            
            return if (response.startsWith("Actividad creada con ID: ")) {
                Log.d("RespuestaNuevaActividad", "Respuesta indica éxito - extrayendo ID...")
                val idString = response.substringAfter("Actividad creada con ID: ")
                Log.d("RespuestaNuevaActividad", "ID extraído como string: '$idString'")
                
                val id = idString.toIntOrNull()
                Log.d("RespuestaNuevaActividad", "ID convertido a Int: $id")
                
                val resultado = RespuestaNuevaActividad(response, id)
                Log.d("RespuestaNuevaActividad", "Objeto creado exitosamente: $resultado")
                resultado
            } else {
                Log.d("RespuestaNuevaActividad", "Respuesta NO indica éxito - creando objeto sin ID")
                val resultado = RespuestaNuevaActividad(response)
                Log.d("RespuestaNuevaActividad", "Objeto creado: $resultado")
                resultado
            }
        }
    }
}