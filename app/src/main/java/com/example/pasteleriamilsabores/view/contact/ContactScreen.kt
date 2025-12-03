package com.example.pasteleriamilsabores.view.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // 💡 NECESARIO para el Intent de llamada
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.viewmodel.contact.ContactViewModel
import com.google.android.gms.maps.model.CameraPosition // 💡 IMPORT DE MAPS
import com.google.android.gms.maps.model.LatLng // 💡 IMPORT DE MAPS
import com.google.maps.android.compose.* // 💡 IMPORT DE MAPS (Contiene GoogleMap, Marker, rememberCameraPositionState)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(nav: NavController, vm: ContactViewModel = viewModel()) {
    // 💡 CONSUMIR EL ESTADO DEL VIEWMODEL
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current // Contexto para iniciar intents (llamada, mapa)

    // 📌 Ubicación de la Pastelería Mil Sabores
    val sucursalLocation = state.location

    // Configuración inicial de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sucursalLocation, state.zoomLevel) // <-- Resuelve CameraPosition
    }

    Scaffold( // <-- Resuelve Scaffold
        topBar = {
            TopAppBar(
                title = { Text("Contacto y Ubicación") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Información de la Sucursal",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // ----------------------------------------------------
            // Teléfono (con acción de llamada)
            // ----------------------------------------------------
            ContactInfoItem(
                icon = Icons.Filled.Phone,
                title = "Teléfono",
                detail = state.phone,
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(vm.getPhoneUri()))
                    context.startActivity(intent)
                }
            )
            Divider(Modifier.padding(vertical = 4.dp))

            // ----------------------------------------------------
            // Correo Electrónico
            // ----------------------------------------------------
            ContactInfoItem(
                icon = Icons.Filled.Email,
                title = "Correo Electrónico",
                detail = state.email,
                onClick = { /* Podrías agregar un Intent de correo */ }
            )
            Divider(Modifier.padding(vertical = 4.dp))

            // ----------------------------------------------------
            // Dirección (con acción de Abrir en Maps)
            // ----------------------------------------------------
            ContactInfoItem(
                icon = Icons.Filled.LocationOn,
                title = "Dirección",
                detail = state.address,
                onClick = {
                    // Intent para abrir Google Maps con la ubicación
                    val gmmIntentUri = Uri.parse("geo:${state.location.latitude},${state.location.longitude}?q=${Uri.encode(state.address)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }
            )
            Divider(Modifier.padding(vertical = 4.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // ----------------------------------------------------
            // Mapa de Google Maps
            // ----------------------------------------------------
            Text(
                text = state.branchName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GoogleMap( // <-- Resuelve GoogleMap
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                Marker( // <-- Resuelve Marker
                    state = rememberMarkerState(position = sucursalLocation),
                    title = state.branchName,
                    snippet = "¡Ven por tu pastel favorito!"
                )
            }
        }
    }
}

// 💡 Nuevo composable para hacer clic en los items de contacto
@Composable
fun ContactInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    detail: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp).padding(end = 4.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelSmall)
                Text(text = detail, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}