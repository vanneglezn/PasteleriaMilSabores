package com.example.pasteleriamilsabores.viewmodel.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// 💡 La clase OrderRepository se importa, pero ahora se usa por inyección
import com.example.pasteleriamilsabores.data.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estados posibles del pedido (orden cronológico)
enum class OrderStatus(val label: String) {
    PENDIENTE_PAGO("Pendiente de pago"),
    CONFIRMADO("Confirmado"),
    EN_ELABORACION("En elaboración"),
    EN_RUTA("En ruta"),
    ENTREGADO("Entregado");

    companion object {
        val ordered = listOf(PENDIENTE_PAGO, CONFIRMADO, EN_ELABORACION, EN_RUTA, ENTREGADO)
    }
}

// Estado de UI que consume la vista
data class TrackingUiState(
    val buyerName: String = "",
    val orderNumber: String = "",
    val status: OrderStatus = OrderStatus.PENDIENTE_PAGO
)

// ViewModel para Tracking (solo lectura del estado actual)
// 💡 CAMBIO CRUCIAL: ACEPTAR OrderRepository en el constructor
class TrackingViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState

    // Carga los datos desde el repositorio
    fun load(orderNumber: String, buyerName: String) {
        viewModelScope.launch {
            // 💡 USAR LA INSTANCIA INYECTADA, Y APROVECHAR QUE ES SUSPEND
            val order = orderRepository.get(orderNumber)
            _uiState.update {
                it.copy(
                    orderNumber = orderNumber,
                    buyerName = buyerName,
                    // El campo 'order' ahora es OrderModel, que tiene la propiedad 'status'
                    status = order?.status ?: OrderStatus.PENDIENTE_PAGO
                )
            }
        }
    }
}