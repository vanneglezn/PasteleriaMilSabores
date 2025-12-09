package com.example.pasteleriamilsabores.data.api

import com.google.gson.annotations.SerializedName


data class ApiProduct(
    val id: String,
    val name: String,
    val unitPrice: Int,
    val qty: Int = 1,
    val category: String = "TORTA",       // Valor por defecto = no rompe la app
    val isBestSeller: Boolean = false     // Ahora no es obligatorio en el JSON
)

