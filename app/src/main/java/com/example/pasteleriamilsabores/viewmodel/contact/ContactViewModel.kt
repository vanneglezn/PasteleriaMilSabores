package com.example.pasteleriamilsabores.viewmodel.contact


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.android.gms.maps.model.LatLng

// 1. Estado de la UI para la pantalla de contacto
data class ContactUiState(
    val branchName: String = "Pastelería Mil Sabores - Sucursal Centro",
    val phone: String = "+52 55 1234 5678", // Reemplazar con el teléfono real
    val email: String = "contacto@milsabores.com",
    val address: String = "Plaza de Armas S/N, Santiago",
    // 💡 COORDENADAS DE SANTIAGO CENTRO (Latitud: -33.4378, Longitud: -70.6504)
    val location: LatLng = LatLng(-33.4378, -70.6504),
    val zoomLevel: Float = 15f
)

// 2. ViewModel
class ContactViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState

    fun getPhoneUri(): String {
        return "tel:${_uiState.value.phone.replace(" ", "")}"
    }
}