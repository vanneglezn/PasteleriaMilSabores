package com.example.pasteleriamilsabores.data

import com.example.pasteleriamilsabores.data.local.dao.UserDao
import com.example.pasteleriamilsabores.data.local.entity.UserEntity
import com.example.pasteleriamilsabores.viewmodel.register.RegisterUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val userDao: UserDao) {

    // Ahora recibe RegisterUiState sin parámetros separados ✔
    suspend fun registerUser(state: RegisterUiState): Boolean = withContext(Dispatchers.IO) {

        // Verificar si el correo ya existe
        val exists = userDao.getUserByEmail(state.email)
        if (exists != null) return@withContext false

        val user = UserEntity(
            fullName = state.fullName,
            phone = state.phone,
            email = state.email,
            passwordHash = state.password // luego se encripta
        )

        userDao.insertUser(user)
        return@withContext true
    }


    suspend fun authenticate(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email)
        return@withContext user != null && user.passwordHash == password
    }
}
