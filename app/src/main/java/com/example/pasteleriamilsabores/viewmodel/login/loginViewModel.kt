package com.example.pasteleriamilsabores.viewmodel.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val message: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // --- Validaciones internas ---
    private fun validateEmail(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "Ingresa tu correo"
        if (!trimmed.contains("@")) return "El formato del correo electrónico no es válido"
        // Si prefieres algo más robusto, descomenta:
        // if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) return "Formato de correo inválido"
        return null
    }

    private fun validatePassword(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "La contraseña no puede estar vacía"
        if (trimmed.length < 6) return "Mínimo 6 caracteres"
        return null
    }

    private fun recomputeForm() {
        val s = _uiState.value
        val emailErr = validateEmail(s.email)
        val passErr  = validatePassword(s.password)
        _uiState.update {
            it.copy(
                emailError = emailErr,
                passError = passErr,
                isFormValid = (emailErr == null && passErr == null && !it.isLoading)
            )
        }
    }

    // --- Intent: cambios desde la UI ---
    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
        recomputeForm()
    }

    fun onPasswordChange(newPass: String) {
        _uiState.update { it.copy(password = newPass) }
        recomputeForm()
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    // --- Submit ---
    fun submitLogin(
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        // Fuerza validación por si vienen vacíos
        recomputeForm()
        val s = _uiState.value
        if (!s.isFormValid) {
            onError("Revisa los campos")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            // Simulación de login remoto
            delay(600)

            // 🔓 ACEPTA cualquier email/pass que haya pasado validaciones
            val ok = true

            if (ok) {
                _uiState.update { it.copy(isLoading = false, message = "¡Bienvenida!") }
                onSuccess()
            } else {
                val msg = "Credenciales inválidas"
                _uiState.update { it.copy(isLoading = false, message = msg) }
                onError(msg)
            }
        }
    }
}
