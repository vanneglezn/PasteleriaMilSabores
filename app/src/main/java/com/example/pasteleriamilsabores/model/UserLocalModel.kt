package com.example.pasteleriamilsabores.model


import android.content.Context
import android.content.SharedPreferences

class UserLocalModel(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

    // Guarda los datos del usuario
    fun guardarUsuario(nombre: String, correo: String, telefono: String) {
        val editor = prefs.edit()
        editor.putString("nombre", nombre)
        editor.putString("correo", correo)
        editor.putString("telefono", telefono)
        editor.apply()
    }

    // Devuelve los datos guardados (si existen)
    fun obtenerUsuario(): Map<String, String> {
        return mapOf(
            "nombre" to (prefs.getString("nombre", "") ?: ""),
            "correo" to (prefs.getString("correo", "") ?: ""),
            "telefono" to (prefs.getString("telefono", "") ?: "")
        )
    }
}
