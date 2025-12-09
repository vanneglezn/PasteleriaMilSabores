package com.example.pasteleriamilsabores.data

import com.example.pasteleriamilsabores.data.local.dao.OrderDao
import com.example.pasteleriamilsabores.data.local.entity.OrderEntity
import com.example.pasteleriamilsabores.data.local.entity.OrderItemEntity
import com.example.pasteleriamilsabores.data.local.entity.OrderWithItems
import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.viewmodel.tracking.OrderStatus
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext


data class OrderModel(
    val id: String,
    val items: List<CartItem>,
    val total: Int,
    var status: OrderStatus,
    val createdAt: String
)


class OrderRepository(private val orderDao: OrderDao) {

    private fun OrderWithItems.toDomainModel() = OrderModel(
        id = order.id,
        items = items.map { CartItem(it.itemId, it.name, it.unitPrice, it.qty) },
        total = order.total,
        status = order.status,
        createdAt = order.createdAt
    )

    private fun List<CartItem>.toItemEntities(orderId: String) = this.map {
        OrderItemEntity(orderId, it.id, it.name, it.unitPrice, it.qty)
    }

    // Crear un pedido nuevo en estado pendiente (usando Room) */
    suspend fun create(items: List<CartItem>, total: Int): String = withContext(Dispatchers.IO) {
        val stamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val id = "MS-$stamp"

        // 1. Crear entidades
        val orderEntity = OrderEntity(
            id,
            total,
            OrderStatus.PENDIENTE_PAGO,
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )
        val itemEntities = items.toItemEntities(id)

        // 2. Guardar en Room
        orderDao.insertOrder(orderEntity)
        orderDao.insertOrderItems(itemEntities)

        return@withContext id
    }

    /** Obtener pedido (como modelo de dominio) */
    suspend fun get(id: String): OrderModel? = withContext(Dispatchers.IO) {
        return@withContext orderDao.getOrderWithItemsFlow(id)
            .firstOrNull() // Obtiene el valor actual del Flow y lo cierra
            ?.toDomainModel()
    }

    /** Marcar como pagado → CONFIRMADO */
    suspend fun markPaid(id: String) = withContext(Dispatchers.IO) {
        val order = getOrderEntity(id) ?: return@withContext
        orderDao.updateOrder(order.copy(status = OrderStatus.CONFIRMADO))
    }

    /** Pasar a elaboración (luego de pagado) */
    suspend fun moveToPreparation(id: String) = withContext(Dispatchers.IO) {
        val order = getOrderEntity(id) ?: return@withContext
        orderDao.updateOrder(order.copy(status = OrderStatus.EN_ELABORACION))
    }

    //  Función auxiliar para obtener solo la entidad sin los items
    private suspend fun getOrderEntity(id: String): OrderEntity? =
        orderDao.getOrderWithItemsFlow(id).firstOrNull()?.order

    /** Limpiar pedidos (opcional, para pruebas) - necesitarías agregar un método DELETE en el DAO */
    // fun clear() { /* orderDao.clearAllTables() */ }

    // NOTA: Los métodos 'advanceForStaff' también requerirían ser implementados de forma 'suspend'
    // y usar el DAO.
}