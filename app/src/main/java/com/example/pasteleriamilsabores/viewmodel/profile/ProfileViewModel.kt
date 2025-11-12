package com.example.pasteleriamilsabores.viewmodel.profile

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.pasteleriamilsabores.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    // Estado actual del perfil (flujo observable)
    private val _user = MutableStateFlow(
        UserProfile(
            nombre = "",
            telefono = "",
            correo = "",
            edad = "",
            beneficio = ""
        )
    )
    val user: StateFlow<UserProfile> = _user.asStateFlow()

    /** 🔹 Carga lo guardado localmente por RegisterScreen */
    fun loadFromPrefs(context: Context) {
        val sp = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        _user.update {
            it.copy(
                nombre   = sp.getString("nombre", "") ?: "",
                telefono = sp.getString("telefono", "") ?: "",
                correo   = sp.getString("correo", "") ?: ""
            )
        }
    }

    /** 🔹 Guarda y actualiza el estado (local + memoria) */
    fun saveToPrefs(context: Context, profile: UserProfile) {
        val sp = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        sp.edit {
            putString("nombre", profile.nombre)
            putString("telefono", profile.telefono)
            putString("correo", profile.correo)
        }
        _user.update { profile }
    }

    /** 🔹 Actualiza solo en memoria (sin guardar en prefs) */
    fun updateProfile(newProfile: UserProfile) {
        _user.update { newProfile }
    }


    /** 🔹 Limpia los datos locales y el estado al cerrar sesión */
    fun logout(context: Context) {
        val sp = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        sp.edit { clear() } // elimina todas las claves del archivo userPrefs

        _user.update {
            UserProfile(
                nombre = "",
                telefono = "",
                correo = "",
                edad = "",
                beneficio = ""
            )
        }
    }
}
