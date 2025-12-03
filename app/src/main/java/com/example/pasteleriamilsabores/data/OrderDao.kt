package com.example.pasteleriamilsabores.data


import androidx.room.*
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    // Insertar los items asociados a un pedido
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // Actualizar solo el estado de un pedido (ej. de PENDIENTE_PAGO a CONFIRMADO)
    @Update
    suspend fun updateOrder(order: OrderEntity)
}