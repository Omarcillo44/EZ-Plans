package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.NuevoPlan.DatosNuevoPlan
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class NuevoPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    companion object {
        private const val TAG = "NuevoPlanViewModel"
    }

    // Estados para la UI
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    private val _planCreated = mutableStateOf(false)
    val planCreated: State<Boolean> = _planCreated

    // Actualización del método crearNuevoPlan en tu ViewModel
    fun crearNuevoPlan(
        datosPlan: DatosNuevoPlan,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Iniciando creación de nuevo plan")
        Log.d(TAG, "Datos del plan a enviar: $datosPlan")

        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null
        _planCreated.value = false

        viewModelScope.launch {
            try {
                Log.d(TAG, "Obteniendo token de autenticación...")
                val token = prefs.leerToken() ?: run {
                    val errorMsg = "Token no disponible"
                    Log.e(TAG, errorMsg)
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                    return@launch
                }
                Log.d(TAG, "Token obtenido exitosamente")

                Log.d(TAG, "Enviando petición POST a /planes/nuevo_plan")

                val response = apiService.crearNuevoPlan(
                    datosPlan = datosPlan,
                    token = "Bearer $token"
                )

                Log.d(TAG, "Respuesta recibida - Código: ${response.code()}")
                Log.d(TAG, "Respuesta recibida - Mensaje: ${response.message()}")

                if (response.isSuccessful) {
                    // Manejar ResponseBody
                    val responseText = if (response.body() != null) {
                        response.body()!!.string() // Para ResponseBody
                    } else {
                        "Plan creado exitosamente"
                    }

                    Log.d(TAG, "Plan creado exitosamente: $responseText")

                    _successMessage.value = responseText
                    _planCreated.value = true

                    onSuccess(responseText)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    Log.e(TAG, "Error en la respuesta: $errorMsg")
                    Log.e(TAG, "Error body: $errorBody")

                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.localizedMessage}"
                Log.e(TAG, "Excepción durante la creación del plan: $errorMsg")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")

                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Finalizando proceso de creación de plan")
            }
        }
    }

    // Función para limpiar estados
    fun limpiarEstados() {
        Log.d(TAG, "Limpiando estados del ViewModel")
        _errorMessage.value = null
        _successMessage.value = null
        _planCreated.value = false
    }

    // Función para validar datos antes de enviar
    fun validarDatosPlan(datosPlan: DatosNuevoPlan): Pair<Boolean, String?> {
        Log.d(TAG, "Validando datos del plan...")
        
        return when {
            datosPlan.titulo.isBlank() -> {
                Log.w(TAG, "Validación fallida: Título vacío")
                Pair(false, "El título del plan no puede estar vacío")
            }
            datosPlan.fechaPlan.isBlank() -> {
                Log.w(TAG, "Validación fallida: Fecha vacía")
                Pair(false, "La fecha del plan no puede estar vacía")
            }
            datosPlan.miembros.isEmpty() -> {
                Log.w(TAG, "Validación fallida: Sin miembros")
                Pair(false, "El plan debe tener al menos un miembro")
            }
            datosPlan.miembros.none { it.administrador } -> {
                Log.w(TAG, "Validación fallida: Sin administrador")
                Pair(false, "El plan debe tener al menos un administrador")
            }
            else -> {
                Log.d(TAG, "Validación exitosa")
                Pair(true, null)
            }
        }
    }
}