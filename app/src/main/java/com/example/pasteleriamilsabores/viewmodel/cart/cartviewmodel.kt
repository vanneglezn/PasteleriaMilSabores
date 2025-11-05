package com.example.pasteleriamilsabores.viewmodel.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Int = 0,
    val isEmpty: Boolean = true
)

class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    private val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    val uiState: StateFlow<CartUiState> =
        items.map { list ->
            val total = list.sumOf { it.unitPrice * it.qty }
            CartUiState(
                items = list,
                total = total,
                isEmpty = list.isEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartUiState()
        )

    fun setInitialItems(initial: List<CartItem>) {
        if (_items.value.isEmpty()) {
            _items.value = initial
        }
    }

    fun add(item: CartItem) = viewModelScope.launch {
        val current = _items.value.toMutableList()
        val idx = current.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            val existing = current[idx]
            current[idx] = existing.copy(qty = existing.qty + item.qty)
        } else {
            current.add(item)
        }
        _items.value = current
    }

    fun updateQty(id: String, newQty: Int) = viewModelScope.launch {
        if (newQty <= 0) {
            remove(id)
            return@launch
        }
        _items.value = _items.value.map {
            if (it.id == id) it.copy(qty = newQty) else it
        }
    }

    fun remove(id: String) = viewModelScope.launch {
        _items.value = _items.value.filterNot { it.id == id }
    }

    fun clear() = viewModelScope.launch {
        _items.value = emptyList()
    }

    fun confirmPurchase(onConfirmed: () -> Unit) {
        // Aquí podrías guardar la orden/llamar repo/etc.
        onConfirmed()
    }
}
