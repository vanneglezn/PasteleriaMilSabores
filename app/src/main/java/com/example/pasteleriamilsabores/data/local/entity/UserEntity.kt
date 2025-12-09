package com.example.pasteleriamilsabores.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    // Usaremos el correo electrónico como clave principal, ya que es único
    @PrimaryKey val email: String,
    val fullName: String,
    val phone: String,
    // Nota: La contraseña nunca se debe guardar en texto plano, pero por simplicidad de este ejercicio, la incluiremos.
    val passwordHash: String
)