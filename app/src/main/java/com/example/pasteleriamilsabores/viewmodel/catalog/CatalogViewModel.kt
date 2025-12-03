package com.example.pasteleriamilsabores.viewmodel.catalog

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
// 💡 NUEVOS IMPORTS
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    val query: String = "",
    val products: List<ProductUi> = emptyList(),
    // 💡 NUEVOS ESTADOS PARA MANEJAR LA CARGA DE LA API
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
    private val _uiState = MutableStateFlow(
        CatalogUiState(
            selected = Category.TODOS,
            products = emptyList(), // 💡 Se inicializa vacío
            isLoading = true // 💡 Se empieza a cargar
        )
    )
    val uiState: StateFlow<CatalogUiState> = _uiState

    // 💡 INICIA LA CARGA DE PRODUCTOS DESDE LA API
    init {
        loadProducts()
    }

    // 💡 FUNCIÓN ASÍNCRONA PARA LLAMAR AL REPOSITORIO
    private fun loadProducts() {
        viewModelScope.launch {
            // Asegura que isLoading sea true al inicio
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Llama a la función suspend del repositorio (que trae los datos del Gist)
                val products = ProductRepository.getProducts()

                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        // Si el catálogo cargado es el de fallback, muestra un error
                        errorMessage = if (products.size == 8 && products.first().item.name.contains("Torta")) "Error al cargar desde el Gist. Mostrando datos de respaldo." else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de red: No se pudo cargar el catálogo."
                    )
                }
            }
        }
    }

    fun selectCategory(cat: Category) = _uiState.update { it.copy(selected = cat) }
    fun updateQuery(q: String)      = _uiState.update { it.copy(query = q) }
}