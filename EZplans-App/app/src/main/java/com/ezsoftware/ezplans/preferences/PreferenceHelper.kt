package com.ezsoftware.ezplans.preferences

import android.content.Context

class PreferenceHelper(context: Context) {
    private val prefs = context.getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE)

    fun guardarEstadoModoOscuro(isDarkMode: Boolean) {
        prefs.edit().putBoolean("modo_oscuro", isDarkMode).apply()
    }

    fun leerEstadoModoOscuro(): Boolean {
        return prefs.getBoolean("modo_oscuro", false)
    }


    // guardar el token
    fun guardarToken(token: String) {
        prefs.edit().putString("token", token).apply()
        println(token)
    }

    // obtener el número de celular almacenado
    fun leerToken(): String? {
        return prefs.getString("token", null)
    }


    // guardar el número de celular
    fun guardarNumeroCelular(celular: String) {
        prefs.edit().putString("numero_celular", celular).apply()

        println(celular)
    }

    fun leerNumeroCelular(): String? {
        return prefs.getString("numero_celular", null)
    }

    fun guardarNombreUsuario(nombre: String) {
        prefs.edit().putString("nombre_usuario", nombre).apply()

    }

    fun leerNombreUsuario(): String? {
        return prefs.getString("nombre_usuario", null)
    }

    fun guardarApellidoUsuario(apellido: String) {
        prefs.edit().putString("apellido_usuario", apellido).apply()

    }

    fun leerApellidoUsuario(): String? {
        return prefs.getString("apellido_usuario", null)
    }

    fun guardarIDUsuario(id: Int) {
        prefs.edit().putInt("id", id).apply()

    }

    fun leerIDUsuario(): Int? {
        return prefs.getInt("id", -1).takeIf { it != -1 } // Retorna null si no existe
    }

}