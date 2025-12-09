@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pasteleriamilsabores.view.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
        // 💡 AJUSTE: Usar Weights para igualar la fila de ítems
    ) {
        Text("Producto", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.5f))
        Text("Cantidad", fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp)) // Ancho fijo
        Text("Precio", fontWeight = FontWeight.SemiBold, modifier = Modifier.width(80.dp), textAlign = TextAlign.End) // Ancho fijo y alineación
    }
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFFCCFE3))
}

// ------- Fila de item (MEJORADA) -------
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Producto (Toma la mayor parte del espacio)
        Text(
            text = item.name,
            modifier = Modifier.weight(1.5f), // 💡 AUMENTAR peso (1.5f) para darle más espacio
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium
        )

        // 2. Control de Cantidad (Grupo de botones)
        Row(
            // 💡 AJUSTE: Dar un ancho fijo a la columna Cantidad para evitar compresión
            modifier = Modifier.width(90.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp) // Espacio entre botones
        ) {
            // Botón de Restar (-)
            OutlinedButton(
                onClick = {
                    val newQty = (item.qty - 1).coerceAtLeast(0)
                    if (newQty == 0) onRemove(item.id) else onQtyChange(item.id, newQty)
                },
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.size(30.dp), // 💡 TAMAÑO FIJO para el botón
                shape = RoundedCornerShape(10.dp)
            ) { Text("-", fontSize = 12.sp) }

            // Cantidad (x3)
            Text(
                text = "x${item.qty}",
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            // Botón de Sumar (+)
            OutlinedButton(
                onClick = { onQtyChange(item.id, item.qty + 1) },
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.size(30.dp), // 💡 TAMAÑO FIJO para el botón
                shape = RoundedCornerShape(10.dp)
            ) { Text("+", fontSize = 12.sp) }
        }

        // 3. Precio (Alineado a la derecha)
        val lineTotal = item.unitPrice * item.qty
        Text(
            text = formatCLP(lineTotal),
            // 💡 AJUSTE: Darle un ancho fijo (80.dp) para alineación y espacio
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold
        )
    }
    HorizontalDivider(color = Color(0xFFFFEEF6), thickness = 1.dp)
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
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.End) // Alinear el total a la derecha
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
                CartItem("1", "Torta tres leches", 14000, 3),
                CartItem("2", "Kuchen Frambuesa", 12000, 2),
                CartItem("3", "Cheesecake Maracuyá", 13500, 1)
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
