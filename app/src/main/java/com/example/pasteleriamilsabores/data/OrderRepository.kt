package com.example.pasteleriamilsabores.data


import com.example.pasteleriamilsabores.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object OrderRepository {
    private val _orders = MutableStateFlow<Map<String, Order>>(emptyMap())
    val orders = _orders.asStateFlow()

    private fun genTracking(): String {
        val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
        val rnd = (1..4).joinToString("") { ('A'..'Z').random().toString() }
        return "MS-$stamp-$rnd"      // ej: MS-20251112-ABCD
    }

    fun create(items: List<CartItem>, total: Int): String {
        val id = genTracking()
        _orders.value = _orders.value.toMutableMap().apply {
            put(id, Order(id, items, total, OrderStatus.PENDIENTE_PAGO))
        }
        return id
    }

    fun markPaid(id: String) {
        _orders.value[id]?.let { old ->
            _orders.value = _orders.value.toMutableMap().apply {
                put(id, old.copy(status = OrderStatus.PAGADO))
            }
        }
    }

    fun advanceStatus(id: String) {
        _orders.value[id]?.let { o ->
            val next = when (o.status) {
                OrderStatus.PAGADO -> OrderStatus.PREPARANDO
                OrderStatus.PREPARANDO -> OrderStatus.EN_CAMINO
                OrderStatus.EN_CAMINO -> OrderStatus.ENTREGADO
                else -> o.status
            }
            if (next != o.status) {
                _orders.value = _orders.value.toMutableMap().apply {
                    put(id, o.copy(status = next))
                }
            }
        }
    }

    fun get(id: String): Order? = _orders.value[id]
}
