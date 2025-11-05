package com.example.pasteleriamilsabores.view.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.viewmodel.tracking.TrackingViewModel
import com.example.pasteleriamilsabores.viewmodel.tracking.OrderStatus
import com.example.pasteleriamilsabores.viewmodel.tracking.TrackingUiState

@Composable
fun TrackingRoute(
    orderNumber: String,
    buyerName: String,
    onGoProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: TrackingViewModel = viewModel()
) {
    LaunchedEffect(orderNumber, buyerName) { viewModel.load(orderNumber, buyerName) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TrackingScreen(
        state = state,
        onNextStatus = { viewModel.advance() },
        onGoProfile = onGoProfile,
        onLogout = onLogout                     // se pasa a la vista
    )
}

@Composable
fun TrackingScreen(
    state: TrackingUiState,
    onNextStatus: () -> Unit,
    onGoProfile: () -> Unit,
    onLogout: () -> Unit                       // 👈 NUEVO
) {
    val steps = OrderStatus.ordered
    val currentIndex = steps.indexOf(state.status).coerceAtLeast(0)
    val progress = if (steps.lastIndex > 0) currentIndex.toFloat() / steps.lastIndex else 0f

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Text(
            "¡Gracias por tu compra, ${state.buyerName}!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text("N° de pedido: ${state.orderNumber}")
        Spacer(Modifier.height(12.dp))

        AssistChip(onClick = {}, label = { Text(state.status.label) })
        Spacer(Modifier.height(8.dp))

        Text("Progreso del pedido", style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Text(
            steps.joinToString(" → ") { it.label },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEachIndexed { i, s ->
                    val active = i == currentIndex
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dot = if (i <= currentIndex)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(dot, shape = MaterialTheme.shapes.small)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            s.label,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (active) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (i < steps.lastIndex) HorizontalDivider(Modifier.padding(start = 20.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botones superiores
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onGoProfile, modifier = Modifier.weight(1f)) {
                Text("Ir a Perfil")
            }
            Button(
                onClick = onNextStatus,
                enabled = state.status != OrderStatus.ENTREGADO,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.ChevronRight, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Siguiente estado")
            }
        }

        Spacer(Modifier.weight(1f)) // empuja el botón final hacia abajo

        //  Botón Cerrar sesión
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onLogout,                  // 👈 cierra sesión de verdad
                modifier = Modifier.fillMaxWidth(0.7f)
            ) { Text("Cerrar sesión") }
        }
    }
}
