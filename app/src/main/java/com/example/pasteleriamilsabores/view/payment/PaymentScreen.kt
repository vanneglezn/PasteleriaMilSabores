package com.example.pasteleriamilsabores.view.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.data.OrderRepository
import java.text.NumberFormat
import java.util.*

@Composable
fun PaymentScreen(
    trackingId: String,
    onPaid: (String) -> Unit,
    onBack: () -> Unit
) {
    val order = remember(trackingId) { OrderRepository.get(trackingId) }
    val nf = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

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
                // 🔸 Estado de error si no hay pedido cargado
                Text(
                    text = "No se encontró el pedido o no se creó correctamente.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onBack) {
                    Text("Volver al carrito")
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
                    text = nf.format(order.total),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        OrderRepository.markPaid(trackingId)
                        OrderRepository.moveToPreparation(trackingId)
                        onPaid(trackingId)
                    },
                    enabled = order != null,   // ← esto quita el warning y protege el click
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Pagar ahora") }


            }
        }
    }
}
