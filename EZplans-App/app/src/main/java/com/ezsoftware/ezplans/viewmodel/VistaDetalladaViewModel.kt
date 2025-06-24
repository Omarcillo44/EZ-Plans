package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.vistaDetallada.DatosVistaDetalladaPlan
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class VistaDetalladaViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    // Estados para la UI
    private val _planState = mutableStateOf<DatosVistaDetalladaPlan?>(null)
    val planState: State<DatosVistaDetalladaPlan?> = _planState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun obtenerDetallesPlan(
        idPlan: Int,
        onSuccess: (DatosVistaDetalladaPlan?) -> Unit = {} // Nuevo parámetro callback
    ) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val token = prefs.leerToken() ?: run {
                    _errorMessage.value = "Token no disponible"
                    return@launch
                }

                val response = apiService.obtenerVistaDetalladaPlan(
                    idPlan = idPlan,
                    token = "Bearer $token"
                )

                if (response.isSuccessful) {
                    _planState.value = response.body()
                    Log.d("PlanDetallado", "Datos recibidos: ${response.body()}")
                    onSuccess(response.body()) // Ejecuta el callback con los datos
                } else {
                    _errorMessage.value = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("PlanDetallado", "Error: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}