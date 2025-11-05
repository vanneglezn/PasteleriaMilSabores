package com.example.pasteleriamilsabores.data


import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.viewmodel.catalog.Category
import com.example.pasteleriamilsabores.viewmodel.catalog.ProductUi

object ProductRepository {
    fun getProducts(): List<ProductUi> = listOf(
        ProductUi(CartItem("1", "Torta de chocolate", 15000, 1), R.drawable.torta_chocolate, Category.TORTA, isBestSeller = true),
        ProductUi(CartItem("2", "Torta tres leches", 14000, 1), R.drawable.torta_tres_leches, Category.TORTA),
        ProductUi(CartItem("3", "Torta frutilla crema", 15500, 1), R.drawable.torta_frutilla_crema, Category.TORTA),
        ProductUi(CartItem("4", "Torta moka", 16000, 1), R.drawable.torta_moka, Category.TORTA),
        ProductUi(CartItem("5", "Torta selva negra", 16500, 1), R.drawable.torta_selva_negra, Category.TORTA, isBestSeller = true),
        ProductUi(CartItem("6", "Cheesecake maracuyá", 13500, 1), R.drawable.cheesecake_maracuya, Category.TORTA),
        ProductUi(CartItem("7", "Kuchen de frambuesa", 12000, 1), R.drawable.kuchen_frambuesa, Category.KUCHEN),
        ProductUi(CartItem("8", "Kuchen de manzana", 11500, 1), R.drawable.kuchen_manzana, Category.KUCHEN)
    )
}
