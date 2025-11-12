package com.example.pasteleriamilsabores.data

import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.viewmodel.tracking.OrderStatus
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object OrderRepository {

    data class Order(
        val id: String,
        val items: List<CartItem>,
        val total: Int,
        var status: OrderStatus = OrderStatus.PENDIENTE_PAGO,
        val createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    )

    private val orders = ConcurrentHashMap<String, Order>()

    /** ✅ Crear un pedido nuevo en estado pendiente */
    fun create(items: List<CartItem>, total: Int): String {
        val stamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val id = "MS-$stamp"
        val order = Order(id = id, items = items, total = total)
        orders[id] = order
        return id
    }

    /**  Obtener pedido */
    fun get(id: String): Order? = orders[id]

    /**  Marcar como pagado → CONFIRMADO */
    fun markPaid(id: String) {
        orders[id]?.status = OrderStatus.CONFIRMADO
    }

    /**  Pasar a elaboración (luego de pagado) */
    fun moveToPreparation(id: String) {
        orders[id]?.status = OrderStatus.EN_ELABORACION
    }

    /**  Avanzar para uso del administrador (no cliente) */
    fun advanceForStaff(id: String) {
        val order = orders[id] ?: return
        order.status = when (order.status) {
            OrderStatus.EN_ELABORACION -> OrderStatus.EN_RUTA
            OrderStatus.EN_RUTA -> OrderStatus.ENTREGADO
            else -> order.status
        }
    }

    /**  Limpiar pedidos (útil en pruebas) */
    fun clear() {
        orders.clear()
    }
}
