package com.example.pasteleriamilsabores.viewmodel.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.model.CartItem


enum class Category { TODOS, TORTA, KUCHEN }

data class ProductUi(
    val item: CartItem,
    val imageRes: Int,
    val category: Category,
    val isBestSeller: Boolean = false
)

data class CatalogUiState(
    val selected: Category = Category.TODOS,
    val query: String = "",
    val products: List<ProductUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val filtered: List<ProductUi>
        get() {
            val base = if (selected == Category.TODOS) products else products.filter { it.category == selected }
            val q = query.trim().lowercase()
            return if (q.isEmpty()) base else base.filter { it.item.name.lowercase().contains(q) }
        }
}

class CatalogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState(isLoading = true))
    val uiState: StateFlow<CatalogUiState> = _uiState

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val products = ProductRepository.getProducts()
                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar el catálogo."
                    )
                }
            }
        }
    }

    fun selectCategory(cat: Category) = _uiState.update { it.copy(selected = cat) }
    fun updateQuery(q: String) = _uiState.update { it.copy(query = q) }
}
