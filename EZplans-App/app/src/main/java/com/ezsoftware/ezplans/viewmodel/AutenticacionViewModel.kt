package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.acceso.AccesoRequest
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch
class AutenticacionViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    fun login(celular: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        println("🔐 [Login] Iniciando login con celular: $celular")

        viewModelScope.launch {
            try {
                val response = apiService.login(AccesoRequest(celular, password))
                println("📡 [Login] Código de respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val token = response.body()?.jwtToken ?: ""
                    println("✅ [Login] Token recibido: $token")

                    prefs.guardarNumeroCelular(celular)
                    prefs.guardarToken(token)

                    obtenerDatosUsuario(celular, token, onError)
                    onSuccess()
                } else {
                    println("❌ [Login] Error en la respuesta: ${response.errorBody()?.string()}")
                    onError("Error al iniciar sesión")
                }
            } catch (e: Exception) {
                println("💥 [Login] Excepción al conectar: ${e.localizedMessage}")
                onError("Error de conexión")
            }
        }
    }

    private suspend fun obtenerDatosUsuario(celularUsuario: String, token: String, onError: (String) -> Unit) {
        println("👤 [Usuario] Solicitando datos del usuario: $celularUsuario")

        try {
            val response = apiService.obtenerDatosUsuario(celularUsuario, "Bearer $token")
            println("📡 [Usuario] Código de respuesta: ${response.code()}")

            when {
                response.isSuccessful -> {
                    val datosUsuario = response.body()
                    if (datosUsuario != null) {
                        prefs.guardarNumeroCelular(datosUsuario.celularUsuario)
                        prefs.guardarNombreUsuario(datosUsuario.nombreUsuario)
                        prefs.guardarApellidoUsuario(datosUsuario.apellidoUsuario)
                        prefs.guardarIDUsuario(datosUsuario.idUsuario)

                        println("✅ [Usuario] Preferencias guardadas correctamente:")
                        println("   ID: ${prefs.leerIDUsuario()}")
                        println("   Nombre: ${prefs.leerNombreUsuario()}")
                        println("   Apellido: ${prefs.leerApellidoUsuario()}")
                    } else {
                        println("⚠️ [Usuario] Respuesta exitosa pero sin datos.")
                        onError("Datos del usuario vacíos.")
                    }
                }
                response.code() == 403 -> {
                    println("⛔ [Usuario] Token inválido o expirado (403)")
                    onError("Sesión inválida, por favor inicia sesión nuevamente.")
                }
                else -> {
                    println("❌ [Usuario] Error inesperado: ${response.errorBody()?.string()}")
                    onError("Error al obtener los datos del usuario.")
                }
            }
        } catch (e: Exception) {
            println("💥 [Usuario] Excepción al obtener datos: ${e.localizedMessage}")
            onError("Error de conexión al obtener datos del usuario.")
        }
    }
}
