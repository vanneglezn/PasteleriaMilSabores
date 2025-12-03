package com.example.pasteleriamilsabores.data


import com.example.pasteleriamilsabores.viewmodel.register.RegisterUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val userDao: UserDao) {

    // Función para guardar el nuevo usuario
    suspend fun registerUser(state: RegisterUiState): Boolean = withContext(Dispatchers.IO) {
        val user = UserEntity(
            email = state.email,
            fullName = state.fullName,
            phone = state.phone,
            passwordHash = state.password // NOTA: Aquí se guardaría el hash real.
        )
        // Intentar guardar. Si tiene éxito, devuelve true
        userDao.insertUser(user)
        return@withContext true
    }

    // Función para verificar credenciales durante el Login
    suspend fun authenticate(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email)

        // Verifica si el usuario existe y si la contraseña coincide.
        // En un app real, se compararía el hash.
        return@withContext user != null && user.passwordHash == password
    }
}