// EliminarPlanViewModel.kt
package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.planes.RespuestaEliminarPlan
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class EliminarPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    // Estados para la UI
    private val _respuestaState = mutableStateOf<RespuestaEliminarPlan?>(null)
    val respuestaState = _respuestaState

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = _errorMessage

    fun eliminarPlan(
        idPlan: Int,
        onComplete: (RespuestaEliminarPlan?) -> Unit = {}
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _respuestaState.value = null

        Log.d("EliminarPlanVM", "Iniciando eliminación del plan ID: $idPlan")
        
        viewModelScope.launch {
            try {
                val token = prefs.leerToken() ?: run {
                    _errorMessage.value = "Token no disponible"
                    Log.e("EliminarPlanVM", "Error: Token no disponible")
                    return@launch
                }

                Log.d("EliminarPlanVM", "Token obtenido, realizando petición...")
                
                val response = apiService.eliminarPlan(
                    idPlan = idPlan,
                    token = "Bearer $token"
                )

                Log.d("EliminarPlanVM", "Respuesta recibida - Código: ${response.code()}")
                
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        Log.d("EliminarPlanVM", "Respuesta del servidor: $body")
                        val resultado = RespuestaEliminarPlan.fromString(body.toString())
                        _respuestaState.value = resultado
                        onComplete(resultado)
                    } ?: run {
                        _errorMessage.value = "Respuesta vacía del servidor"
                        Log.e("EliminarPlanVM", "Error: Respuesta vacía del servidor")
                    }
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    _errorMessage.value = errorMsg
                    Log.e("EliminarPlanVM", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                Log.e("EliminarPlanVM", errorMsg, e)
            } finally {
                _isLoading.value = false
                Log.d("EliminarPlanVM", "Operación de eliminación finalizada")
            }
        }
    }
}