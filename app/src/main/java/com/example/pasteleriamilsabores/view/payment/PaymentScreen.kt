package com.example.pasteleriamilsabores.view.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.data.OrderRepository // 💡 Ahora es la CLASE, no el OBJECT
import com.example.pasteleriamilsabores.data.OrderModel // 💡 Importar el nuevo modelo de dominio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope // 💡 Necesario para scope.launch
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@Composable
fun PaymentScreen(
    trackingId: String,
    orderRepository: OrderRepository, // 💡 RECIBE EL NUEVO REPOSITORIO
    onPaid: (String) -> Unit,
    onBack: () -> Unit
) {
    // 💡 Estado mutable para guardar el pedido (OrderModel es la clase pública del repositorio)
    var order by remember { mutableStateOf<OrderModel?>(null) }
    val nf = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val scope = rememberCoroutineScope() // 💡 Scope para tareas asíncronas

    // Cargar el pedido de Room al iniciar la pantalla
    LaunchedEffect(trackingId) {
        order = orderRepository.get(trackingId) // 💡 Llama al método suspend
    }

    Scaffold(
        topBar = {
            // Encabezado superior simple y compatible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pago",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(onClick = onBack) { Text("Volver") }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (order == null) {
                // 🔸 Estado de carga o error
                Text(
                    text = "Cargando pedido o no se encontró el ID: $trackingId",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onBack) {
                    Text("Volver al catálogo")
                }
            } else {
                // 🔹 Mostrar datos del pedido
                Text(
                    text = "N° de seguimiento:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = trackingId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Total a pagar:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = nf.format(order!!.total), // Usar !! con cuidado, o usar 'order?.total'
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        scope.launch { // 💡 ENVUELVE LA LLAMADA EN UNA CORRUTINA
                            orderRepository.markPaid(trackingId)
                            orderRepository.moveToPreparation(trackingId)
                            onPaid(trackingId)
                        }
                    },
                    enabled = order != null,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Pagar ahora") }
            }
        }
    }
}