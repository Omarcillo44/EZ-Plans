package com.ezsoftware.ezplans.repository

import com.ezsoftware.ezplans.models.acceso.AccesoRequest
import com.ezsoftware.ezplans.network.RetrofitInstance

class AuthRepository {
    //Solicita el data class de acceso
    suspend fun login(request: AccesoRequest) = RetrofitInstance.api.login(request)
}