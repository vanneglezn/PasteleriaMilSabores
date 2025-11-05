package com.example.pasteleriamilsabores.viewmodel.register


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

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

class RegisterViewModel : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui

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

    private fun validate(s: RegisterUiState): Map<String, String> {
        val e = mutableMapOf<String, String>()

        if (s.fullName.trim().isEmpty()) e["fullName"] = "Ingresa tu nombre."
        if (s.phone.isBlank()) e["phone"] = "Ingresa tu celular."
        else if (!s.phone.all { it.isDigit() }) e["phone"] = "Solo dígitos."
        else if (s.phone.length !in 8..12) e["phone"] = "Largo 8–12."

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        if (s.email.isBlank()) e["email"] = "Ingresa tu correo."
        else if (!emailRegex.matches(s.email)) e["email"] = "Correo inválido."

        if (s.password.length < 6) e["password"] = "Mínimo 6 caracteres."

        return e
    }

    /**
     * Simula el registro. Aquí luego llamas a tu repositorio/API.
     */
    suspend fun submit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val errs = validate(_ui.value)
        if (errs.isNotEmpty()) {
            _ui.update { it.copy(errors = errs, canSubmit = false) }
            onError("Revisa los campos.")
            return
        }
        _ui.update { it.copy(isLoading = true) }
        try {
            // TODO: reemplazar por llamada real (repo/API)
            // delay(800)
            _ui.update { it.copy(isLoading = false, success = true) }
            onSuccess()
        } catch (ex: Exception) {
            _ui.update { it.copy(isLoading = false) }
            onError("No se pudo registrar, intenta de nuevo.")
        }
    }
}
