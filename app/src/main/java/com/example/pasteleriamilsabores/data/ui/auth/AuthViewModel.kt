package com.example.pasteleriamilsabores.data.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.data.AuthRepository
import com.example.pasteleriamilsabores.viewmodel.register.RegisterUiState // <- IMPORT UNIFICADO
import com.example.pasteleriamilsabores.data.ui.auth.LoginUiState // si tienes LoginUiState en este paquete, o ajusta import

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var registerUiState = mutableStateOf(RegisterUiState()) // ahora usa la de viewmodel.register
        private set

    var loginUiState = mutableStateOf(LoginUiState())
        private set

    fun register(onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch {
            val ok = authRepository.registerUser(registerUiState.value) // pasa el objeto único
            if (ok) onSuccess() else onFail()
        }
    }

    fun login(onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch {
            val ok = authRepository.authenticate(
                loginUiState.value.email,
                loginUiState.value.password
            )
            if (ok) onSuccess() else onFail()
        }
    }
}
