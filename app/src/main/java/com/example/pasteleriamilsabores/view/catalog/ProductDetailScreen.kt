package com.example.pasteleriamilsabores.view.catalog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

private fun clp(n: Int): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
    nf.maximumFractionDigits = 0
    return nf.format(n)
}

@Composable
fun ProductDetailScreen(
    productName: String,
    price: Int,
    @DrawableRes imageRes: Int? = null,          // ← si lo pasas, muestra imagen; si no, usa ícono
    onAddToCart: (quantity: Int, message: String) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var message by remember { mutableStateOf("") }
    val maxChars = 60
    val total = price * quantity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen o ícono
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Cake,
                contentDescription = productName,
                modifier = Modifier.size(160.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(productName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text(clp(price), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.height(20.dp))

        // Cantidad
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Cantidad", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = { if (quantity > 1) quantity-- },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) { Text("−") }

            Text(quantity.toString(), style = MaterialTheme.typography.titleMedium)

            OutlinedButton(
                onClick = { if (quantity < 99) quantity++ },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) { Text("+") }
        }

        Spacer(Modifier.height(16.dp))

        // Mensaje personalizado
        OutlinedTextField(
            value = message,
            onValueChange = { if (it.length <= maxChars) message = it },
            label = { Text("Mensaje para la torta (opcional)") },
            supportingText = {
                Text("${message.length} / $maxChars")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        // Total + agregar al carrito
        Button(
            onClick = { onAddToCart(quantity, message.trim()) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Agregar ${quantity} al carrito · ${clp(total)}")
        }
    }
}
