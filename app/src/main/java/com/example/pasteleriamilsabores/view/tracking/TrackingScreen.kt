package com.example.pasteleriamilsabores.view.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.viewmodel.tracking.OrderStatus
import com.example.pasteleriamilsabores.viewmodel.tracking.TrackingUiState
import com.example.pasteleriamilsabores.viewmodel.tracking.TrackingViewModel

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
        onGoProfile = onGoProfile,
        onLogout = onLogout
    )
}

@Composable
fun TrackingScreen(
    state: TrackingUiState,
    onGoProfile: () -> Unit,
    onLogout: () -> Unit
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
        Text("Seguimiento de pedido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("¡Gracias por tu compra, ${state.buyerName}!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("N° de pedido: ${state.orderNumber}")

        Spacer(Modifier.height(12.dp))
        AssistChip(onClick = {}, label = { Text(steps[currentIndex].label) })

        Spacer(Modifier.height(12.dp))
        Text("Progreso del pedido", style = MaterialTheme.typography.titleMedium)
        // Compose Material3 reciente permite pasar lambda; si tu versión usa Float directo, cambia a 'progress = progress'
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            steps.joinToString(" → ") { it.label },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEachIndexed { i, s ->
                    val active = i == currentIndex
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dotColor =
                            if (i <= currentIndex) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        Box(
                            Modifier.size(10.dp)
                                .background(dotColor, shape = MaterialTheme.shapes.small)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = s.label,
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onGoProfile, modifier = Modifier.weight(1f)) {
                Text("Ir a Perfil")
            }
            // 👇 Sin botón de “Siguiente estado”: el cliente no puede cambiar estados
        }

        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(0.7f)) {
                Text("Cerrar sesión")
            }
        }
    }
}
