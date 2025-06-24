// EditarPlanViewModel.kt
package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.planes.DatosVistaEditarPlan
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class VistaEditarPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)
    private val TAG = "EditarPlanVM"

    // Estados para la UI
    private val _planData = mutableStateOf<DatosVistaEditarPlan?>(null)
    val planData = _planData

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = _errorMessage

    fun obtenerDatosPlan(
        idPlan: Int,
        onComplete: (DatosVistaEditarPlan?) -> Unit = {}
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _planData.value = null

        Log.d(TAG, "Solicitando datos para editar plan ID: $idPlan")
        
        viewModelScope.launch {
            try {
                val token = prefs.leerToken() ?: run {
                    _errorMessage.value = "Token no disponible"
                    Log.e(TAG, "Error: Token no disponible")
                    return@launch
                }

                Log.d(TAG, "Token obtenido, realizando petición...")
                
                val response = apiService.obtenerDatosEditarPlan(
                    idPlan = idPlan,
                    token = "Bearer $token"
                )

                Log.d(TAG, "Respuesta recibida - Código: ${response.code()}")
                
                when {
                    response.isSuccessful -> {
                        response.body()?.let { datosPlan ->
                            Log.d(TAG, "Datos recibidos: $datosPlan")
                            _planData.value = datosPlan
                            onComplete(datosPlan)
                        } ?: run {
                            _errorMessage.value = "Datos no encontrados"
                            Log.e(TAG, "Error: Respuesta vacía")
                        }
                    }
                    response.code() == 404 -> {
                        _errorMessage.value = "Plan no encontrado"
                        Log.e(TAG, "Error: Plan no encontrado")
                    }
                    else -> {
                        val errorMsg = "Error ${response.code()}: ${response.message()}"
                        _errorMessage.value = errorMsg
                        Log.e(TAG, errorMsg)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                Log.e(TAG, errorMsg, e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Operación finalizada")
            }
        }
    }

    fun limpiarEstados() {
        Log.d(TAG, "Limpiando estados del ViewModel")
        _errorMessage.value = null
        _planData.value = null
    }
}