package com.example.pasteleriamilsabores.viewmodel.tracking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// Estados posibles del pedido
enum class OrderStatus(val label: String) {
    CONFIRMADO("Pedido confirmado"),
    EN_ELABORACION("En elaboración"),
    EN_RUTA("En ruta"),
    LISTO_PARA_ENTREGA("Listo para la entrega"),
    ENTREGADO("Entregado");

    companion object {
        val ordered = listOf(CONFIRMADO, EN_ELABORACION, EN_RUTA, LISTO_PARA_ENTREGA, ENTREGADO)
        fun next(s: OrderStatus) = ordered.getOrNull(ordered.indexOf(s) + 1) ?: ENTREGADO
    }
}

// Estado de UI que consume la View
data class TrackingUiState(
    val buyerName: String = "",
    val orderNumber: String = "",
    val status: OrderStatus = OrderStatus.CONFIRMADO
)

class TrackingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState

    fun load(orderNumber: String, buyerName: String) {
        _uiState.update { it.copy(orderNumber = orderNumber, buyerName = buyerName) }
    }

    fun advance() {
        _uiState.update { it.copy(status = OrderStatus.next(it.status)) }
    }
}
