package com.example.pasteleriamilsabores.viewmodel.catalog

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.pasteleriamilsabores.data.ProductRepository
import com.example.pasteleriamilsabores.model.CartItem

enum class Category { TODOS, TORTA, KUCHEN }

data class ProductUi(
    val item: CartItem,
    @DrawableRes val imageRes: Int,
    val category: Category,
    val isBestSeller: Boolean = false
)

data class CatalogUiState(
    val selected: Category = Category.TODOS,
    val products: List<ProductUi> = emptyList()
) {
    val filtered: List<ProductUi>
        get() = if (selected == Category.TODOS) products else products.filter { it.category == selected }
}

class CatalogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        CatalogUiState(
            selected = Category.TODOS,
            products = ProductRepository.getProducts()
        )
    )
    val uiState: StateFlow<CatalogUiState> = _uiState

    fun selectCategory(cat: Category) {
        _uiState.update { it.copy(selected = cat) }
    }
}
