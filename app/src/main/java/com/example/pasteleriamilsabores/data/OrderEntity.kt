package com.example.pasteleriamilsabores.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.TypeConverter
import com.example.pasteleriamilsabores.viewmodel.tracking.OrderStatus
import com.google.gson.Gson // Se asume Gson para el TypeConverter

// --- Conversor de Tipo para Room (para almacenar OrderStatus) ---
class Converters {
    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String {
        return value.name
    }

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus {
        return OrderStatus.valueOf(value)
    }
}


// --- Entidades (Tablas) de la Base de Datos ---

// 1. Entidad principal para el Pedido (La cabecera)
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val total: Int,
    val status: OrderStatus, // Usará el TypeConverter
    val createdAt: String
)

// 2. Entidad para los items dentro de cada pedido
@Entity(tableName = "order_items", primaryKeys = ["orderId", "itemId"])
data class OrderItemEntity(
    val orderId: String, // Clave foránea al OrderEntity
    val itemId: String,
    val name: String,
    val unitPrice: Int,
    val qty: Int
)

// 3. Clase que une Order con sus Items (Necesaria para leer el pedido completo)
data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)