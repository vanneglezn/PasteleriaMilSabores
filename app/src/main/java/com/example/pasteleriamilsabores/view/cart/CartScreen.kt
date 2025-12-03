package com.example.pasteleriamilsabores.view.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasteleriamilsabores.model.CartItem
import java.text.NumberFormat
import java.util.Locale

// ------- Utilidad: formato CLP -------
private fun formatCLP(value: Int): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    nf.maximumFractionDigits = 0
    return nf.format(value)
}

// ------- TopBar con degradé -------
@Composable
private fun GradientTopBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF9A8D4),
            Color(0xFFF9A8D4)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ------- Encabezado de tabla -------
@Composable
private fun TableHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Producto", fontWeight = FontWeight.SemiBold)
        Text("Cantidad", fontWeight = FontWeight.SemiBold)
        Text("Precio", fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFFCCFE3))
}

// ------- Fila de item -------
@Composable
private fun CartItemRow(
    item: CartItem,
    onQtyChange: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.name,
            modifier = Modifier.weight(1.4f),
            maxLines = 1
        )

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = {
                    val newQty = (item.qty - 1).coerceAtLeast(0)
                    if (newQty == 0) onRemove(item.id) else onQtyChange(item.id, newQty)
                },
                contentPadding = PaddingValues(horizontal = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("-") }

            Text(
                text = "x${item.qty}",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontWeight = FontWeight.Medium
            )

            OutlinedButton(
                onClick = { onQtyChange(item.id, item.qty + 1) },
                contentPadding = PaddingValues(horizontal = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("+") }
        }

        val lineTotal = item.unitPrice * item.qty
        Text(
            text = formatCLP(lineTotal),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Medium
        )
    }
    Divider(color = Color(0xFFFFEEF6), thickness = 1.dp)
}

// ------- Pantalla principal -------
@Composable
fun CartScreen(
    items: List<CartItem>,
    onQtyChange: (id: String, newQty: Int) -> Unit,
    onRemove: (id: String) -> Unit,
    onConfirm: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val total = remember(items) { items.sumOf { it.unitPrice * it.qty } }

    Column(Modifier.fillMaxSize()) {
        GradientTopBar(title = "Mi carrito", onBack = onBack)

        if (items.isEmpty()) {
            // Estado vacío
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tu carrito está vacío", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Agrega productos desde el catálogo para continuar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { onBack?.invoke() }) {
                    Text("Volver al catálogo")
                }
            }
            return
        }

        // Contenido con items
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            TableHeader()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
            ) {
                items(items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onQtyChange = onQtyChange,
                        onRemove = onRemove
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Total: ${formatCLP(total)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBCFE8),
                    contentColor = Color(0xFF9D174D)
                ),
                enabled = total > 0
            ) {
                Text("Confirmar compra", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ------- Preview -------
@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {
    var cart by remember {
        mutableStateOf(
            listOf(
                CartItem("1", "Torta Vivi", 18000, 1),
                CartItem("2", "Kuchen Frambuesa", 12000, 2)
            )
        )
    }

    fun updateQty(id: String, qty: Int) {
        cart = cart.map { if (it.id == id) it.copy(qty = qty) else it }
    }

    fun remove(id: String) {
        cart = cart.filterNot { it.id == id }
    }

    MaterialTheme(colorScheme = lightColorScheme()) {
        CartScreen(
            items = cart,
            onQtyChange = ::updateQty,
            onRemove = ::remove,
            onConfirm = { /* flujo de pago */ },
            onBack = { /* navigateUp() */ }
        )
    }
}
