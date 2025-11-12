package com.example.pasteleriamilsabores.model


enum class OrderStatus { PENDIENTE_PAGO, PAGADO, PREPARANDO, EN_CAMINO, ENTREGADO }

data class Order(
    val id: String,                  // tracking
    val items: List<CartItem>,
    val total: Int,
    val status: OrderStatus
)
