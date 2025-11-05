package com.example.pasteleriamilsabores.model


data class CartItem(
    val id: String,
    val name: String,
    val unitPrice: Int, // precio en CLP
    val qty: Int
)
