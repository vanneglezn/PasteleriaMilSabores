package com.example.pasteleriamilsabores.data.api

data class ApiProduct(
    val id: String,
    val name: String,
    val price: Int,
    val category: String, // Ej: "TORTA", "KUCHEN"
    val isBestSeller: Boolean? = false
)