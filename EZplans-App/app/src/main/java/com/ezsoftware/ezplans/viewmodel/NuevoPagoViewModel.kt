package com.ezsoftware.ezplans.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezsoftware.ezplans.models.Pagos.DatosNuevoPago
import com.ezsoftware.ezplans.network.ApiService
import com.ezsoftware.ezplans.network.RetrofitInstance
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import kotlinx.coroutines.launch
import java.math.BigDecimal

class NuevoPagoViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitInstance.api
    private val prefs = PreferenceHelper(application)

    companion object {
        private const val TAG = "NuevoPagoViewModel"
    }

    // Estados para la UI
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    private val _pagoRegistrado = mutableStateOf(false)
    val pagoRegistrado: State<Boolean> = _pagoRegistrado

    fun registrarNuevoPago(
        datosPago: DatosNuevoPago,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Iniciando registro de nuevo pago")
        Log.d(TAG, "Datos del pago a enviar: $datosPago")
        
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null
        _pagoRegistrado.value = false

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

                Log.d(TAG, "Enviando petición POST a /pagos/nuevo_pago")
                Log.d(TAG, "ID Deuda: ${datosPago.idDeuda}")
                Log.d(TAG, "Monto Pago: ${datosPago.montoPago}")
                Log.d(TAG, "Forma Pago: ${if (datosPago.formaPago) "Efectivo" else "Transferencia"}")
                Log.d(TAG, "Comprobante: ${datosPago.comprobantePago?.takeIf { it.isNotBlank() } ?: "Sin comprobante"}")

                val response = apiService.registrarNuevoPago(
                    datosPago = datosPago,
                    token = "Bearer $token"
                )

                Log.d(TAG, "Respuesta recibida - Código: ${response.code()}")
                Log.d(TAG, "Respuesta recibida - Mensaje: ${response.message()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d(TAG, "Pago registrado exitosamente: $responseBody")
                    
                    _successMessage.value = (responseBody ?: "Pago registrado con éxito").toString()
                    _pagoRegistrado.value = true
                    
                    onSuccess((responseBody ?: "Pago registrado con éxito").toString())
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
                Log.e(TAG, "Excepción durante el registro del pago: $errorMsg")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Finalizando proceso de registro de pago")
            }
        }
    }

    // Función para limpiar estados
    fun limpiarEstados() {
        Log.d(TAG, "Limpiando estados del ViewModel")
        _errorMessage.value = null
        _successMessage.value = null
        _pagoRegistrado.value = false
    }

    // Función para validar datos del pago
    fun validarDatosPago(datosPago: DatosNuevoPago): Pair<Boolean, String?> {
        Log.d(TAG, "Validando datos del pago...")
        Log.d(TAG, "Validando: ID Deuda=${datosPago.idDeuda}, Monto=${datosPago.montoPago}")
        
        return when {
            datosPago.idDeuda <= 0 -> {
                Log.w(TAG, "Validación fallida: ID de deuda inválido")
                Pair(false, "El ID de la deuda debe ser mayor a 0")
            }
            datosPago.montoPago <= BigDecimal.ZERO -> {
                Log.w(TAG, "Validación fallida: Monto inválido")
                Pair(false, "El monto del pago debe ser mayor a 0")
            }
            datosPago.montoPago.scale() > 2 -> {
                Log.w(TAG, "Validación fallida: Demasiados decimales en el monto")
                Pair(false, "El monto no puede tener más de 2 decimales")
            }
            !datosPago.formaPago && datosPago.comprobantePago.isNullOrBlank() -> {
                Log.w(TAG, "Validación fallida: Transferencia sin comprobante")
                Pair(false, "Las transferencias requieren comprobante de pago")
            }
            else -> {
                Log.d(TAG, "Validación exitosa")
                Pair(true, null)
            }
        }
    }

    // Función para formatear el monto como string para mostrar
    fun formatearMonto(monto: BigDecimal): String {
        return "$${String.format("%.2f", monto)}"
    }

    // Función para obtener descripción de forma de pago
    fun obtenerDescripcionFormaPago(formaPago: Boolean): String {
        return if (formaPago) "Efectivo" else "Transferencia"
    }

    // Función para crear objeto DatosNuevoPago
    fun crearDatosPago(
        idDeuda: Int,
        montoPago: String,
        formaPago: Boolean,
        comprobantePago: String?
    ): DatosNuevoPago? {
        Log.d(TAG, "Creando objeto DatosNuevoPago")
        Log.d(TAG, "Parámetros: idDeuda=$idDeuda, monto=$montoPago, formaPago=$formaPago")
        
        return try {
            val montoDecimal = BigDecimal(montoPago)
            val datos = DatosNuevoPago(
                idDeuda = idDeuda,
                montoPago = montoDecimal,
                formaPago = formaPago,
                comprobantePago = comprobantePago?.takeIf { it.isNotBlank() }
            )
            Log.d(TAG, "Objeto DatosNuevoPago creado exitosamente: $datos")
            datos
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Error al convertir monto a BigDecimal: $montoPago")
            null
        }
    }
}