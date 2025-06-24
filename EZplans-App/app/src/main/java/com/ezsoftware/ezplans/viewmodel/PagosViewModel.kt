// PagosViewModel.kt
package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.DatosVistaPago
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch

class PagosViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)
    private val TAG = "PagosVM"

    // Estados para la UI
    private val _pagos = mutableStateOf<List<DatosVistaPago>>(emptyList())
    val pagos: State<List<DatosVistaPago>> = _pagos

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _isEmpty = mutableStateOf(false)
    val isEmpty: State<Boolean> = _isEmpty

    fun obtenerPagosUsuario(
        idUsuario: Int,
        onComplete: (Boolean) -> Unit = {}
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _isEmpty.value = false
        _pagos.value = emptyList()

        Log.d(TAG, "Solicitando pagos para usuario ID: $idUsuario")

        viewModelScope.launch {
            try {
                val token = prefs.leerToken() ?: run {
                    _errorMessage.value = "Token no disponible"
                    Log.e(TAG, "Error: Token no disponible")
                    return@launch
                }

                val response = apiService.obtenerPagosUsuario(
                    idUsuario = idUsuario,
                    token = "Bearer $token"
                )

                Log.d(TAG, "Respuesta recibida - Código: ${response.code()}")

                when {
                    response.isSuccessful -> {
                        response.body()?.let { listaPagos ->
                            if (listaPagos.isEmpty()) {
                                _isEmpty.value = true
                                Log.d(TAG, "El usuario no tiene pagos registrados")
                            } else {
                                _pagos.value = listaPagos
                                Log.d(TAG, "Pagos obtenidos: ${listaPagos.size} registros")
                            }
                            onComplete(true)
                        } ?: run {
                            _errorMessage.value = "Datos no disponibles"
                            Log.e(TAG, "Error: Respuesta vacía")
                        }
                    }
                    response.code() == 204 -> {
                        _isEmpty.value = true
                        Log.d(TAG, "El usuario no tiene pagos (204 No Content)")
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
        _pagos.value = emptyList()
        _isEmpty.value = false
    }
}