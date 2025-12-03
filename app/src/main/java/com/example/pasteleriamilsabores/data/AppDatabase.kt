package com.example.pasteleriamilsabores.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Asegúrate de que todas las Entidades (tablas) estén listadas aquí
@Database(
    // 💡 PASO 1: AÑADIR LA NUEVA ENTIDAD DE USUARIO
    entities = [OrderEntity::class, OrderItemEntity::class, UserEntity::class],
    version = 2, // 💡 PASO 2: INCREMENTAR LA VERSIÓN AL AÑADIR UserEntity
    exportSchema = false
)
@TypeConverters(Converters::class) // Usar el conversor para OrderStatus
abstract class AppDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao // 💡 PASO 3: AÑADIR EL NUEVO DAO PARA USUARIOS

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pasteleria_db"
                )
                    // 💡 IMPORTANTE: Esto recrea la DB si el esquema cambia.
                    // Es necesario porque incrementamos la versión (de 1 a 2).
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}