package com.example.pasteleriamilsabores

import com.example.pasteleriamilsabores.viewmodel.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    // Dispatcher que permite controlar el tiempo en corrutinas
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // 💡 1. Inicializa el ViewModel
        viewModel = LoginViewModel()
        // 💡 2. Reemplaza el dispatcher principal (Main) por el de prueba
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // 💡 3. Restaura el dispatcher después de cada prueba
        Dispatchers.resetMain()
    }

    // --- Pruebas de Validación de Campos ---

    @Test
    fun login_onEmailChange_emptyEmailShouldProduceError() = runTest {
        // Verifica que un correo vacío arroja el error correcto
        viewModel.onEmailChange("")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Ingresa tu correo", state.emailError)
    }

    @Test
    fun login_onPasswordChange_shortPasswordShouldProduceError() = runTest {
        // Verifica que una contraseña menor a 6 caracteres arroja error
        viewModel.onPasswordChange("12345")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Mínimo 6 caracteres", state.passError)
    }

    @Test
    fun login_onValidForm_shouldBeValid() = runTest {
        // Verifica que con datos válidos el formulario esté listo para enviarse
        viewModel.onEmailChange("test@milsabores.cl")
        viewModel.onPasswordChange("pasteleria1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.emailError)
        assertNull(state.passError)
        assertTrue(state.isFormValid)
    }

    // --- Prueba de Lógica Asíncrona (Simulación de Login) ---

    @Test
    fun login_onSubmit_onSuccessShouldBeCalled() = runTest {
        // Simula un login exitoso
        viewModel.onEmailChange("test@milsabores.cl")
        viewModel.onPasswordChange("123456")
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false

        // Llama a la función de envío que contiene la corrutina
        viewModel.submitLogin(
            onSuccess = { successCalled = true }
        )
        // Avanza el tiempo del dispatcher para simular la espera del delay(600)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verifica que la función de éxito fue llamada
        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}