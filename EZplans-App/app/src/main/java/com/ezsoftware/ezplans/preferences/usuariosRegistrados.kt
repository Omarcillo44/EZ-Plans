package com.ezsoftware.ezplans.preferences

import android.content.Context

fun obtenerUsuariosDefault(context: Context): List<UsuarioRegistrado> {
    val idActual = PreferenceHelper(context).leerIDUsuario()
    return usuariosDefault.filter { it.id_usuario != idActual }
}

data class UsuarioRegistrado(
    val id_usuario: Int,
    val celular_usuario: String,
    val nombre_usuario: String,
    val apellidos_usuario: String
)

private val usuariosDefault = listOf(
    UsuarioRegistrado(1, "1234567890", "Juan", "EL Caballo"),
    UsuarioRegistrado(2, "5561397608", "Omar", "Lorenzo"),
    UsuarioRegistrado(3, "5663516239", "Jimena", "Garrido Reyes"),
    UsuarioRegistrado(4, "5550430908", "Dulce", "Delgado Vázquez"),
    UsuarioRegistrado(5, "5535027625", "Mauricio", "Teodoro Rosales"),
    UsuarioRegistrado(6, "5578516627", "Donovan", "Rostro Perez"),
    UsuarioRegistrado(13, "0123456789", "Erick", "Huerta Valdepeña")
)