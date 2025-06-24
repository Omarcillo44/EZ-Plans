package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.dashboard.datosDashboard
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    fun obtenerDatosDashboard(
        idUsuario: Int,
        soloCompletos: Boolean?,
        esAdmin: Boolean?,
        onSuccess: (datosDashboard) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = prefs.leerToken() ?: ""

                val response = apiService.obtenerDatosDashboard(
                    idUsuario,
                    soloCompletos,
                    esAdmin,
                    "Bearer $token"
                )

                println("🚀 Respuesta completa del servidor: ${response.body()}")

                if (response.isSuccessful) {



                    response.body()?.let { datos ->
                        Log.d("DASHBOARD", "Datos: $datos")
                        println("📊 Datos procesados correctamente: $datos")
                        onSuccess(datos)
                    } ?: run {
                        println("⚠️ Respuesta vacía")
                        onError("Datos vacíos en la respuesta")
                    }
                } else {
                    println("❌ Error en la API: ${response.code()} - ${response.message()}")
                    onError("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                println("⚡ Error de conexión: ${e.localizedMessage}")
                onError("Error de conexión")
            }
        }
    }

}
