package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.NuevaActividad.Miembros.DatosUsuarioEnPlan
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class MiembrosPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    companion object {
        private const val TAG = "MiembrosPlanViewModel"
    }

    // Estados para la UI
    private val _miembrosState = mutableStateOf<List<DatosUsuarioEnPlan>>(emptyList())
    val miembrosState: State<List<DatosUsuarioEnPlan>> = _miembrosState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _isEmpty = mutableStateOf(false)
    val isEmpty: State<Boolean> = _isEmpty

    fun obtenerMiembrosPlan(
        idPlan: Int,
        onSuccess: (List<DatosUsuarioEnPlan>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Iniciando obtención de miembros del plan")
        Log.d(TAG, "ID del plan solicitado: $idPlan")
        
        _isLoading.value = true
        _errorMessage.value = null
        _isEmpty.value = false

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

                Log.d(TAG, "Enviando petición GET a /planes/miembros")
                Log.d(TAG, "Parámetros: idPlan=$idPlan")

                val response = apiService.obtenerMiembrosPlan(
                    idPlan = idPlan,
                    token = "Bearer $token"
                )

                Log.d(TAG, "Respuesta recibida - Código: ${response.code()}")
                Log.d(TAG, "Respuesta recibida - Mensaje: ${response.message()}")

                if (response.isSuccessful) {
                    val miembros = response.body() ?: emptyList()
                    Log.d(TAG, "Miembros obtenidos exitosamente: ${miembros.size} miembros")
                    
                    // Log detallado de cada miembro
                    miembros.forEachIndexed { index, miembro ->
                        Log.d(TAG, "Miembro $index:")
                        Log.d(TAG, "  - ID: ${miembro.idUsuario}")
                        Log.d(TAG, "  - Nombre: ${miembro.nombreUsuario} ${miembro.apellidosUsuario}")
                        Log.d(TAG, "  - Celular: ${miembro.celularUsuario}")
                    }

                    _miembrosState.value = miembros
                    _isEmpty.value = miembros.isEmpty()
                    
                    if (miembros.isEmpty()) {
                        Log.w(TAG, "No se encontraron miembros para el plan $idPlan")
                    }
                    
                    onSuccess(miembros)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = when (response.code()) {
                        404 -> "No se encontraron miembros para el plan $idPlan"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    
                    Log.e(TAG, "Error en la respuesta: $errorMsg")
                    Log.e(TAG, "Error body: $errorBody")
                    
                    if (response.code() == 404) {
                        _isEmpty.value = true
                        _miembrosState.value = emptyList()
                    }
                    
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.localizedMessage}"
                Log.e(TAG, "Excepción durante la obtención de miembros: $errorMsg")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Finalizando proceso de obtención de miembros")
            }
        }
    }

    // Función para limpiar estados
    fun limpiarEstados() {
        Log.d(TAG, "Limpiando estados del ViewModel")
        _errorMessage.value = null
        _isEmpty.value = false
        _miembrosState.value = emptyList()
    }

    // Función para recargar miembros
    fun recargarMiembros(idPlan: Int) {
        Log.d(TAG, "Recargando miembros del plan $idPlan")
        obtenerMiembrosPlan(idPlan)
    }

    // Función para obtener miembro por ID
    fun obtenerMiembroPorId(idUsuario: Int): DatosUsuarioEnPlan? {
        Log.d(TAG, "Buscando miembro con ID: $idUsuario")
        val miembro = _miembrosState.value.find { it.idUsuario == idUsuario }
        
        if (miembro != null) {
            Log.d(TAG, "Miembro encontrado: ${miembro.nombreUsuario} ${miembro.apellidosUsuario}")
        } else {
            Log.w(TAG, "Miembro con ID $idUsuario no encontrado")
        }
        
        return miembro
    }

    // Función para validar ID del plan
    fun validarIdPlan(idPlan: Int): Pair<Boolean, String?> {
        Log.d(TAG, "Validando ID del plan: $idPlan")
        
        return if (idPlan <= 0) {
            Log.w(TAG, "Validación fallida: ID del plan inválido")
            Pair(false, "El ID del plan debe ser mayor a 0")
        } else {
            Log.d(TAG, "Validación exitosa")
            Pair(true, null)
        }
    }
}