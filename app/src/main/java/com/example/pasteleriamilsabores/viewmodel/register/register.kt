package com.example.pasteleriamilsabores.viewmodel.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI STATE para la pantalla de registro
data class RegisterUiState(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val promoCode: String = "",
    val errors: Map<String, String> = emptyMap(),
    val canSubmit: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false
)

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui

    // === Actualización de campos ===
    fun onNameChange(v: String)      = update { it.copy(fullName = v) }
    fun onPhoneChange(v: String)     = update { it.copy(phone = v) }
    fun onEmailChange(v: String)     = update { it.copy(email = v) }
    fun onPasswordChange(v: String)  = update { it.copy(password = v) }
    fun onPromoChange(v: String)     = update { it.copy(promoCode = v) }

    private fun update(block: (RegisterUiState) -> RegisterUiState) {
        _ui.update { old ->
            val newState = block(old)
            val errs = validate(newState)
            newState.copy(errors = errs, canSubmit = errs.isEmpty())
        }
    }

    // === Validación ===
    private fun validate(s: RegisterUiState): Map<String, String> {
        val e = mutableMapOf<String, String>()

        if (s.fullName.isBlank()) e["fullName"] = "Ingresa tu nombre."
        if (s.phone.isBlank()) e["phone"] = "Ingresa tu teléfono."
        else if (!s.phone.all { it.isDigit() }) e["phone"] = "Solo números."
        else if (s.phone.length !in 8..12) e["phone"] = "Debe estar entre 8 y 12 dígitos."

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(s.email)) e["email"] = "Correo no válido."

        if (s.password.length < 6) e["password"] = "Mínimo 6 caracteres."

        return e
    }

    // === Registrar usuario en ROOM ===
    fun submit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val current = _ui.value

        if (!current.canSubmit) {
            onError("Revisa los campos antes de continuar.")
            return
        }

        _ui.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val success = authRepository.registerUser(_ui.value)

                if (success) {
                    _ui.update { it.copy(isLoading = false, success = true) }
                    onSuccess()
                } else {
                    _ui.update { it.copy(isLoading = false) }
                    onError("Ese correo ya está registrado.")
                }

            } catch (e: Exception) {
                _ui.update { it.copy(isLoading = false) }
                onError("Error al guardar usuario: ${e.message}")
            }
        }
    }
}
