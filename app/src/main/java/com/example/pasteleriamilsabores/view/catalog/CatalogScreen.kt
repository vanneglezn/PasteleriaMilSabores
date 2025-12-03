package com.example.pasteleriamilsabores.view.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.viewmodel.catalog.Category
import com.example.pasteleriamilsabores.viewmodel.catalog.CatalogViewModel
import com.example.pasteleriamilsabores.viewmodel.catalog.ProductUi
import java.text.NumberFormat
import java.util.Locale

// -------- Top bar con buscador --------
@Composable
private fun TopBarWithSearch(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCartClick: (() -> Unit)? = null
) {
    val gradient = Brush.horizontalGradient(listOf(Color(0xFFF9A8D4), Color(0xFFF9A8D4)))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = { Text("Buscar por nombre…") },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            onCartClick?.let {
                Button(
                    onClick = it,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBCFE8),
                        contentColor = Color(0xFF9D174D)
                    ),
                    modifier = Modifier
                        .height(56.dp)
                        .widthIn(min = 120.dp)
                ) { Text("Ver carrito") }
            }
        }
    }
}

// ---------- Utilidad CLP ----------
private fun clp(price: Int): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
    nf.maximumFractionDigits = 0
    return nf.format(price)
}

// ---------- Badge ----------
@Composable
private fun BestsellerBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFFFEEF6),
        contentColor = Color(0xFF9D174D),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 2.dp
    ) {
        Text(
            "Más vendido",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

// ---------- Card ----------
@Composable
private fun ProductCard(
    product: ProductUi,
    onProductDetail: (CartItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box {
                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                if (product.isBestSeller) {
                    BestsellerBadge(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = product.item.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(text = clp(product.item.unitPrice), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onProductDetail(product.item) },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBCFE8),
                    contentColor = Color(0xFF9D174D)
                )
            ) { Text("Ver detalle") }
        }
    }
}

// ---------- Chips estables ----------
@Composable
private fun CategoryFilterStable(
    selected: Category,
    onSelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            Category.TODOS to "Todos",
            Category.TORTA to "Tortas",
            Category.KUCHEN to "Kuchen"
        ).forEach { (cat, label) ->
            val isSelected = selected == cat
            Button(
                onClick = { onSelected(cat) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFFFBCFE8) else Color(0xFFFCE7F3),
                    contentColor = if (isSelected) Color(0xFF9D174D) else Color(0xFF6B7280)
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

// ---------- Pantalla (usa ViewModel con búsqueda) ----------
@Composable
fun CatalogScreen(
    onOpenCart: () -> Unit,
    onProductDetail: (CartItem) -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopBarWithSearch(
            title = "Catálogo",
            query = state.query,
            onQueryChange = { viewModel.updateQuery(it) },
            onClearQuery = { viewModel.updateQuery("") },
            onCartClick = onOpenCart
        )

        CategoryFilterStable(
            selected = state.selected,
            onSelected = { viewModel.selectCategory(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.filtered) { p ->
                ProductCard(product = p, onProductDetail = onProductDetail)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogPreview() {
    CatalogScreen(onOpenCart = {}, onProductDetail = {})
}
