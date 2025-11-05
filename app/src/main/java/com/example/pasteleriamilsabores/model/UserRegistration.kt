package com.example.pasteleriamilsabores.model

data class UserRegistration(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val promoCode: String = ""
)
