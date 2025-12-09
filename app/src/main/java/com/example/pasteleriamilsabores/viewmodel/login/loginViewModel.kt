package com.example.pasteleriamilsabores.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.data.AuthRepository
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

class LoginViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // ================= Validación =================
    private fun validateEmail(v: String): String? =
        if (v.isBlank()) "Campo requerido"
        else if (!v.contains("@")) "Correo no válido"
        else null

    private fun validatePassword(v: String): String? =
        if (v.length < 6) "Mínimo 6 caracteres"
        else null

    private fun validateForm() {
        val s = _uiState.value
        val eEmail = validateEmail(s.email)
        val ePass  = validatePassword(s.password)

        _uiState.update {
            it.copy(
                emailError = eEmail,
                passError = ePass,
                isFormValid = (eEmail == null && ePass == null && !it.isLoading)
            )
        }
    }

    fun onEmailChange(v: String) {
        _uiState.update { it.copy(email = v) }
        validateForm()
    }

    fun onPasswordChange(v: String) {
        _uiState.update { it.copy(password = v) }
        validateForm()
    }

    // ================== LOGIN REAL ==================
    fun submitLogin(
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        validateForm()
        val s = _uiState.value
        if (!s.isFormValid) return onError("Revisa los campos")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val ok = authRepo.authenticate(s.email, s.password)

            if (ok) {
                _uiState.update { it.copy(isLoading = false, message = "Bienvenida!") }
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, message = "Credenciales inválidas") }
                onError("Correo o contraseña incorrectos")
            }
        }
    }
}
