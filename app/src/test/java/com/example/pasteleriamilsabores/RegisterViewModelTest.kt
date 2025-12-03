package com.example.pasteleriamilsabores

import com.example.pasteleriamilsabores.data.AuthRepository
import com.example.pasteleriamilsabores.viewmodel.register.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock // Importar Mockito para simular dependencias

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    // Usamos Mockito para simular el AuthRepository, ya que la prueba unitaria
    // no debe depender de la base de datos Room.
    private val mockAuthRepository = mock<AuthRepository>(AuthRepository::class.java)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Inicializa el ViewModel pasando el mock del repositorio
        viewModel = RegisterViewModel(mockAuthRepository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- PRUEBAS DE VALIDACIÓN DE CAMPOS ---

    @Test
    fun register_emptyRequiredFields_shouldProduceErrors() = runTest {
        // Act: No cambiar ningún campo (todos quedan vacíos)
        viewModel.onNameChange("")
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.onPhoneChange("")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.ui.value

        // Assert: Verificar que hay errores para los 4 campos obligatorios
        assertFalse(state.canSubmit)
        assertEquals(4, state.errors.size)
        assertTrue(state.errors.containsKey("fullName"))
        assertTrue(state.errors.containsKey("email"))
        assertTrue(state.errors.containsKey("phone"))
        assertTrue(state.errors.containsKey("password"))
    }

    @Test
    fun register_invalidEmailFormat_shouldProduceError() = runTest {
        // Act: Ingresar un email sin '@'
        viewModel.onEmailChange("correo_invalido.com")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.ui.value

        // Assert: Verificar el mensaje de error de formato
        assertEquals("Correo inválido.", state.errors["email"])
    }

    @Test
    fun register_invalidPhoneCharacters_shouldProduceError() = runTest {
        // Act: Ingresar caracteres que no son dígitos
        viewModel.onPhoneChange("9876-5432")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.ui.value

        // Assert: Verificar el error de solo dígitos
        assertEquals("Solo dígitos.", state.errors["phone"])
    }

    @Test
    fun register_validForm_shouldBeSubmitable() = runTest {
        // Act: Ingresar datos que cumplen todas las validaciones
        viewModel.onNameChange("Vanessa González")
        viewModel.onPhoneChange("912345678")
        viewModel.onEmailChange("vanessa@milsabores.cl")
        viewModel.onPasswordChange("MiClaveSegura")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.ui.value

        // Assert: Verificar que no hay errores y que el formulario está listo
        assertTrue(state.errors.isEmpty())
        assertTrue(state.canSubmit)
    }

    // --- PRUEBA ASÍNCRONA DE ENVÍO ---

    @Test
    fun register_onSubmissionAttempt_shouldSetLoadingState() = runTest {
        // Arrange: Configurar el formulario como válido
        viewModel.onNameChange("Test User")
        viewModel.onPhoneChange("999999999")
        viewModel.onEmailChange("submit@test.cl")
        viewModel.onPasswordChange("password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Act: Intentar enviar
        viewModel.submit(onSuccess = {}, onError = {})

        // Assert: Verificar que el estado de carga (isLoading) se activa inmediatamente
        assertTrue(viewModel.ui.value.isLoading)
    }
}
