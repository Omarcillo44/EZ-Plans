package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.NuevaActividad.DatosNuevaActividad
import com.ezsoftware.ezplans.models.NuevaActividad.RespuestaNuevaActividad
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class NuevaActividadViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    // Estados para la UI
    private val _respuestaState = mutableStateOf<RespuestaNuevaActividad?>(null)
    val respuestaState: State<RespuestaNuevaActividad?> = _respuestaState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    fun crearNuevaActividad(
        datosActividad: DatosNuevaActividad,
        onSuccess: (RespuestaNuevaActividad) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        Log.d("NuevaActividad", "=== INICIANDO CREACIÓN DE ACTIVIDAD ===")
        Log.d("NuevaActividad", "Estado inicial - isLoading: ${_isLoading.value}, isSuccess: ${_isSuccess.value}")
        
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = false
        
        Log.d("NuevaActividad", "Estados actualizados - isLoading: ${_isLoading.value}")
        Log.d("NuevaActividad", "Datos recibidos para envío:")
        Log.d("NuevaActividad", "  - ID Plan: ${datosActividad.idPlan}")
        Log.d("NuevaActividad", "  - Título: ${datosActividad.titulo}")
        Log.d("NuevaActividad", "  - Monto Total: ${datosActividad.montoTotal}")
        Log.d("NuevaActividad", "  - Detalles: ${datosActividad.detalles}")
        Log.d("NuevaActividad", "  - Cantidad de miembros: ${datosActividad.miembros.size}")
        
        datosActividad.miembros.forEachIndexed { index, miembro ->
            Log.d("NuevaActividad", "  Miembro $index:")
            Log.d("NuevaActividad", "    - ID Usuario: ${miembro.idUsuario}")
            Log.d("NuevaActividad", "    - Aportación: ${miembro.aportacion}")
            Log.d("NuevaActividad", "    - ID Acreedor Deuda: ${miembro.idAcreedorDeuda}")
            Log.d("NuevaActividad", "    - Monto Deuda: ${miembro.montoDeuda}")
            Log.d("NuevaActividad", "    - Monto Correspondiente: ${miembro.montoCorrespondiente}")
        }

        viewModelScope.launch {
            try {
                Log.d("NuevaActividad", "Paso 1: Obteniendo token de autenticación...")
                val token = prefs.leerToken() ?: run {
                    val errorMsg = "Token no disponible"
                    Log.e("NuevaActividad", "ERROR: $errorMsg")
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                    _isLoading.value = false
                    Log.d("NuevaActividad", "Estados finales por error de token - isLoading: ${_isLoading.value}")
                    return@launch
                }
                
                Log.d("NuevaActividad", "Paso 2: Token obtenido exitosamente")
                Log.d("NuevaActividad", "Token (primeros 20 chars): ${token.take(20)}...")
                
                Log.d("NuevaActividad", "Paso 3: Realizando llamada a la API...")
                Log.d("NuevaActividad", "Endpoint: /actividades/nueva_actividad")
                Log.d("NuevaActividad", "Método: POST")
                
                val response = apiService.crearNuevaActividad(
                    datosActividad = datosActividad,
                    token = "Bearer $token"
                )
                
                Log.d("NuevaActividad", "Paso 4: Respuesta recibida de la API")
                Log.d("NuevaActividad", "Status Code: ${response.code()}")
                Log.d("NuevaActividad", "Is Successful: ${response.isSuccessful}")
                Log.d("NuevaActividad", "Response Message: ${response.message()}")
                
                if (response.isSuccessful) {
                    Log.d("NuevaActividad", "Paso 5a: Respuesta exitosa - Procesando body...")
                    val responseBody = response.body()
                    Log.d("NuevaActividad", "Response Body: $responseBody")
                    
                    if (responseBody != null) {
                        Log.d("NuevaActividad", "Paso 6a: Body no nulo - Creando objeto respuesta...")
                        val respuesta = RespuestaNuevaActividad.fromString(responseBody.toString())
                        Log.d("NuevaActividad", "Respuesta procesada:")
                        Log.d("NuevaActividad", "  - Mensaje: ${respuesta.mensaje}")
                        Log.d("NuevaActividad", "  - ID Actividad: ${respuesta.idActividad}")
                        
                        _respuestaState.value = respuesta
                        _isSuccess.value = true
                        
                        Log.d("NuevaActividad", "Paso 7a: Estados actualizados - respuestaState: ${_respuestaState.value}, isSuccess: ${_isSuccess.value}")
                        Log.d("NuevaActividad", "Paso 8a: Ejecutando callback onSuccess...")
                        onSuccess(respuesta)
                        Log.d("NuevaActividad", "=== ACTIVIDAD CREADA EXITOSAMENTE ===")
                    } else {
                        val errorMsg = "Respuesta vacía del servidor"
                        Log.e("NuevaActividad", "ERROR Paso 6b: $errorMsg")
                        _errorMessage.value = errorMsg
                        onError(errorMsg)
                    }
                } else {
                    Log.e("NuevaActividad", "Paso 5b: Respuesta no exitosa - Procesando error...")
                    val errorBody = response.errorBody()?.string()
                    Log.e("NuevaActividad", "Error Body: $errorBody")
                    
                    val errorMsg = when (response.code()) {
                        400 -> {
                            Log.e("NuevaActividad", "Error 400 - Bad Request")
                            errorBody ?: "Datos inválidos o conflicto en la solicitud"
                        }
                        401 -> {
                            Log.e("NuevaActividad", "Error 401 - Unauthorized")
                            "No autorizado - Token inválido"
                        }
                        500 -> {
                            Log.e("NuevaActividad", "Error 500 - Internal Server Error")
                            "Error interno del servidor"
                        }
                        else -> {
                            Log.e("NuevaActividad", "Error ${response.code()} - ${response.message()}")
                            "Error ${response.code()}: ${response.message()}"
                        }
                    }
                    
                    Log.e("NuevaActividad", "Mensaje de error final: $errorMsg")
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                    Log.e("NuevaActividad", "Estados actualizados con error - errorMessage: ${_errorMessage.value}")
                }
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.localizedMessage}"
                Log.e("NuevaActividad", "=== EXCEPCIÓN CAPTURADA ===")
                Log.e("NuevaActividad", "Tipo de excepción: ${e.javaClass.simpleName}")
                Log.e("NuevaActividad", "Mensaje: ${e.localizedMessage}")
                Log.e("NuevaActividad", "Stack trace completo:")
                Log.e("NuevaActividad", e.stackTraceToString())
                
                _errorMessage.value = errorMsg
                onError(errorMsg)
                Log.e("NuevaActividad", "Estados actualizados con excepción - errorMessage: ${_errorMessage.value}")
            } finally {
                _isLoading.value = false
                Log.d("NuevaActividad", "=== FINALIZANDO OPERACIÓN ===")
                Log.d("NuevaActividad", "Estados finales:")
                Log.d("NuevaActividad", "  - isLoading: ${_isLoading.value}")
                Log.d("NuevaActividad", "  - isSuccess: ${_isSuccess.value}")
                Log.d("NuevaActividad", "  - errorMessage: ${_errorMessage.value}")
                Log.d("NuevaActividad", "  - respuestaState: ${_respuestaState.value}")
            }
        }
    }

    // Función para limpiar los estados después de manejar el resultado
    fun limpiarEstados() {
        Log.d("NuevaActividad", "=== LIMPIANDO ESTADOS ===")
        Log.d("NuevaActividad", "Estados antes de limpiar:")
        Log.d("NuevaActividad", "  - respuestaState: ${_respuestaState.value}")
        Log.d("NuevaActividad", "  - errorMessage: ${_errorMessage.value}")
        Log.d("NuevaActividad", "  - isSuccess: ${_isSuccess.value}")
        
        _respuestaState.value = null
        _errorMessage.value = null
        _isSuccess.value = false
        
        Log.d("NuevaActividad", "Estados después de limpiar:")
        Log.d("NuevaActividad", "  - respuestaState: ${_respuestaState.value}")
        Log.d("NuevaActividad", "  - errorMessage: ${_errorMessage.value}")
        Log.d("NuevaActividad", "  - isSuccess: ${_isSuccess.value}")
    }

    // Función para resetear solo el estado de éxito (útil para validaciones en UI)
    fun resetearExito() {
        Log.d("NuevaActividad", "=== RESETEANDO ESTADO DE ÉXITO ===")
        Log.d("NuevaActividad", "isSuccess antes: ${_isSuccess.value}")
        _isSuccess.value = false
        Log.d("NuevaActividad", "isSuccess después: ${_isSuccess.value}")
    }
}