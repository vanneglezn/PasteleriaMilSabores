package com.example.pasteleriamilsabores.viewmodel.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    // Estado del perfil (datos del usuario)
    private val _user = MutableStateFlow(
        UserProfile(
            nombre = "Vanessa González",
            telefono = "+56 9 1234 5678",
            correo = "vanessa@duocuc.cl",
            edad = "24",
            beneficio = "Correo institucional 20%"
        )
    )
    val user: StateFlow<UserProfile> = _user.asStateFlow()

    // Simula la actualización de datos
    fun updateProfile(newProfile: UserProfile) {
        viewModelScope.launch {
            _user.update { newProfile }
        }
    }

    // Aquí podrías manejar cierre de sesión o llamadas a repositorio real
    fun logout() {
        // TODO: limpiar sesión o token si tienes uno
    }
}
