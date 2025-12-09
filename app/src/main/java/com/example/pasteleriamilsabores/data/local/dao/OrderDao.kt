package com.example.pasteleriamilsabores.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.pasteleriamilsabores.data.local.entity.OrderEntity
import com.example.pasteleriamilsabores.data.local.entity.OrderItemEntity
import com.example.pasteleriamilsabores.data.local.entity.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    // Obtener un pedido por ID con todos sus items
    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderWithItemsFlow(orderId: String): Flow<OrderWithItems?>

    // Obtener todos los pedidos (para un historial)
    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersWithItems(): Flow<List<OrderWithItems>>

    // Insertar/actualizar un pedido
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    // Insertar los items asociados a un pedido
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // Actualizar solo el estado de un pedido (ej. de PENDIENTE_PAGO a CONFIRMADO)
    @Update
    suspend fun updateOrder(order: OrderEntity)
}