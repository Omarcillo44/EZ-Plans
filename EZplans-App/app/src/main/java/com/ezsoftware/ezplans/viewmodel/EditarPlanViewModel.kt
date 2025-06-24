// ActualizarPlanViewModel.kt
package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.planes.DatosEditarPlan
import com.ezsoftware.ezplans.models.planes.RespuestaEditarPlan
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class EditarPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)
    private val TAG = "ActualizarPlanVM"

    // Estados para la UI
    private val _respuestaEstado = mutableStateOf<RespuestaEditarPlan?>(null)
    val respuestaEstado: State<RespuestaEditarPlan?> = _respuestaEstado

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun actualizarPlan(
        datosPlan: DatosEditarPlan,
        onComplete: (Boolean) -> Unit = {}
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _respuestaEstado.value = null

        Log.d(TAG, "Iniciando actualización para plan ID: ${datosPlan.idPlan}")
        Log.d(TAG, "Datos enviados: [Título: ${datosPlan.titulo}, Fecha: ${datosPlan.fecha}]")

        viewModelScope.launch {
            try {
                val token = prefs.leerToken() ?: run {
                    _errorMessage.value = "Token no disponible"
                    Log.e(TAG, "Error: Token no disponible")
                    return@launch
                }

                val response = apiService.actualizarPlan(
                    datosPlan = datosPlan,
                    token = "Bearer $token"
                )

                Log.d(TAG, "Respuesta recibida - Código: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val resultado = RespuestaEditarPlan.fromString(body.toString())
                        _respuestaEstado.value = resultado
                        Log.d(TAG, "Actualización exitosa: ${resultado.mensaje}")
                        onComplete(true)
                    } ?: run {
                        _errorMessage.value = "Respuesta vacía del servidor"
                        Log.e(TAG, "Error: El servidor no devolvió datos")
                    }
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    _errorMessage.value = errorMsg
                    Log.e(TAG, errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                Log.e(TAG, errorMsg, e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Finalizada operación de actualización")
            }
        }
    }

    fun limpiarEstados() {
        Log.d(TAG, "Limpiando estados del ViewModel")
        _errorMessage.value = null
        _respuestaEstado.value = null
    }
}