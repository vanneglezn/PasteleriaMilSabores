package com.example.pasteleriamilsabores.data

import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.data.api.ApiProduct
import com.example.pasteleriamilsabores.data.api.RetrofitClient
import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.viewmodel.catalog.Category
import com.example.pasteleriamilsabores.viewmodel.catalog.ProductUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ProductRepository {

    // 💡 Mapea el ID del producto (que viene del API) al recurso drawable local
    private fun getImageResource(id: String): Int? = when (id) {
        "1" -> R.drawable.torta_chocolate
        "2" -> R.drawable.torta_tres_leches
        "3" -> R.drawable.torta_frutilla_crema
        "4" -> R.drawable.torta_moka
        "5" -> R.drawable.torta_selva_negra
        "6" -> R.drawable.cheesecake_maracuya
        "7" -> R.drawable.kuchen_frambuesa
        "8" -> R.drawable.kuchen_manzana
        else -> null
    }

    // 💡 Convierte la categoría (String de la API) al enum local
    private fun mapCategory(cat: String): Category = when (cat.uppercase()) {
        "TORTA" -> Category.TORTA
        "KUCHEN" -> Category.KUCHEN
        else -> Category.TODOS
    }

    // 💡 FUNCIÓN DE FALLBACK (Datos originales hardcodeados, en caso de error de red)
    private fun getHardcodedProducts(): List<ProductUi> = listOf(
        ProductUi(CartItem("1", "Torta de chocolate", 15000, 1), R.drawable.torta_chocolate, Category.TORTA, isBestSeller = true),
        ProductUi(CartItem("2", "Torta tres leches", 14000, 1), R.drawable.torta_tres_leches, Category.TORTA),
        ProductUi(CartItem("3", "Torta frutilla crema", 15500, 1), R.drawable.torta_frutilla_crema, Category.TORTA),
        ProductUi(CartItem("4", "Torta moka", 16000, 1), R.drawable.torta_moka, Category.TORTA),
        ProductUi(CartItem("5", "Torta selva negra", 16500, 1), R.drawable.torta_selva_negra, Category.TORTA, isBestSeller = true),
        ProductUi(CartItem("6", "Cheesecake maracuyá", 13500, 1), R.drawable.cheesecake_maracuya, Category.TORTA),
        ProductUi(CartItem("7", "Kuchen de frambuesa", 12000, 1), R.drawable.kuchen_frambuesa, Category.KUCHEN),
        ProductUi(CartItem("8", "Kuchen de manzana", 11500, 1), R.drawable.kuchen_manzana, Category.KUCHEN)
    )

    /** 💡 FUNCIÓN ASÍNCRONA: Reemplaza los productos hardcodeados con la llamada al Gist */
    suspend fun getProducts(): List<ProductUi> = withContext(Dispatchers.IO) {
        return@withContext try {
            val apiProducts = RetrofitClient.productApiService.getProductsFromGist()

            // Mapea y transforma los datos de la API a nuestro modelo de UI (ProductUi)
            apiProducts.mapNotNull { apiProduct ->
                val imageRes = getImageResource(apiProduct.id)
                if (imageRes != null) {
                    ProductUi(
                        item = CartItem(
                            id = apiProduct.id,
                            name = apiProduct.name,
                            unitPrice = apiProduct.price, // Usa 'price' del JSON
                            qty = 1
                        ),
                        imageRes = imageRes,
                        category = mapCategory(apiProduct.category),
                        isBestSeller = apiProduct.isBestSeller ?: false
                    )
                } else null
            }
        } catch (e: Exception) {
            // Si falla la red, devuelve los productos hardcodeados como fallback
            getHardcodedProducts()
        }
    }
}