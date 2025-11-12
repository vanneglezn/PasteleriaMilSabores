package com.example.pasteleriamilsabores

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.navegation.Screen
import com.example.pasteleriamilsabores.ui.theme.PasteleriaMilSaboresTheme
import com.example.pasteleriamilsabores.view.cart.CartScreen
import com.example.pasteleriamilsabores.view.catalog.CatalogScreen
import com.example.pasteleriamilsabores.view.payment.PaymentScreen
import com.example.pasteleriamilsabores.view.profile.ProfileScreen
import com.example.pasteleriamilsabores.viewmodel.profile.ProfileViewModel
import com.example.pasteleriamilsabores.data.OrderRepository
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.R

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PasteleriaMilSaboresTheme {

                val nav = rememberNavController()

                // ===== Carrito en memoria =====
                val cartItems = remember { mutableStateListOf<CartItem>() }

                fun addToCart(id: String, name: String, price: Int, qty: Int = 1) {
                    val idx = cartItems.indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        val old = cartItems[idx]
                        cartItems[idx] = old.copy(qty = old.qty + qty)
                    } else {
                        cartItems.add(CartItem(id = id, name = name, unitPrice = price, qty = qty))
                    }
                }

                fun updateQty(id: String, newQty: Int) {
                    val idx = cartItems.indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        if (newQty <= 0) cartItems.removeAt(idx)
                        else cartItems[idx] = cartItems[idx].copy(qty = newQty)
                    }
                }

                fun removeItem(id: String) { cartItems.removeAll { it.id == id } }
                fun clearCart() { cartItems.clear() }
                // =======================================

                fun imageForProduct(id: String): Int? = when (id) {
                    "1" -> R.drawable.torta_chocolate
                    "2" -> R.drawable.torta_tres_leches
                    "3" -> R.drawable.torta_frutilla_crema
                    "4" -> R.drawable.torta_moka
                    "5" -> R.drawable.torta_selva_negra
                    "6" -> R.drawable.cheesecake_maracuya
                    "7" -> R.drawable.kuchen_frambuesa
                    "8" -> R.drawable.kuchen_manzana
                    else -> null
                }

                // Drawer y rutas
                val backStackEntry by nav.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val isAuthRoute = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route
                val showDrawer = !isAuthRoute

                fun NavOptionsBuilder.noDupes(popTo: String? = null) {
                    launchSingleTop = true
                    restoreState = true
                    popTo?.let { route -> popUpTo(route) { inclusive = false; saveState = true } }
                }

                // ---------- Transiciones (slide + fade) ----------
                val enterForward = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn()
                val exitForward = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut()
                val enterBack = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) + fadeIn()
                val exitBack = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut()

                // ---------- NavHost ----------
                @Composable
                fun AppNavHost(modifier: Modifier = Modifier) {
                    NavHost(
                        navController = nav,
                        startDestination = Screen.Login.route,
                        modifier = modifier
                    ) {
                        // 🔐 Login
                        composable(Screen.Login.route) {
                            com.example.pasteleriamilsabores.view.login.LoginScreen(
                                onLogin = { nav.navigate(Screen.Catalog.route) { noDupes(popTo = Screen.Login.route) } },
                                onGoRegister = { nav.navigate(Screen.Register.route) { noDupes(popTo = Screen.Login.route) } }
                            )
                        }

                        // 🧾 Registro
                        composable(Screen.Register.route) {
                            com.example.pasteleriamilsabores.view.register.RegisterScreen(
                                onDone = { nav.navigate(Screen.Catalog.route) { noDupes(popTo = Screen.Login.route) } }
                            )
                        }

                        // 🍰 Catálogo
                        composable(Screen.Catalog.route) {
                            CatalogScreen(
                                onOpenCart = { nav.navigate(Screen.Cart.route) { noDupes(popTo = Screen.Catalog.route) } },
                                onProductDetail = { product: CartItem ->
                                    val encodedName = Uri.encode(product.name)
                                    val img = imageForProduct(product.id) ?: -1
                                    nav.navigate("${Screen.ProductDetail.route}/${product.id}/$encodedName/${product.unitPrice}/$img") { noDupes() }
                                }
                            )
                        }

                        // 🛒 Carrito → Crea pedido y pasa a Pago
                        composable(Screen.Cart.route) {
                            CartScreen(
                                items = cartItems.toList(),
                                onQtyChange = { id, newQty -> updateQty(id, newQty) },
                                onRemove = { id -> removeItem(id) },
                                onConfirm = {
                                    val total = cartItems.sumOf { it.unitPrice * it.qty }
                                    val trackingId = OrderRepository.create(cartItems.toList(), total)
                                    nav.navigate("payment/$trackingId") { noDupes(popTo = Screen.Catalog.route) }
                                },
                                onBack = { nav.navigateUp() }
                            )
                        }

                        // 💳 Pago → Tracking
                        composable("payment/{trackingId}") { back ->
                            val trackingId = back.arguments?.getString("trackingId") ?: return@composable
                            PaymentScreen(
                                trackingId = trackingId,
                                onPaid = { id ->
                                    clearCart()
                                    nav.navigate("tracking/$id") { noDupes(popTo = Screen.Catalog.route) }
                                },
                                onBack = { nav.navigateUp() }
                            )
                        }

                        // 🚚 Tracking
                        composable("tracking/{trackingId}") { back ->
                            val trackingId = back.arguments?.getString("trackingId") ?: ""
                            com.example.pasteleriamilsabores.view.tracking.TrackingRoute(
                                orderNumber = trackingId,
                                buyerName = "Vanessa González",
                                onGoProfile = { nav.navigate(Screen.Profile.route) { noDupes(popTo = Screen.Catalog.route) } },
                                onLogout = {
                                    nav.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        // 👤 Perfil
                        composable(Screen.Profile.route) {
                            val context = LocalContext.current
                            val profileVM: ProfileViewModel = viewModel()

                            ProfileScreen(
                                onLogout = {
                                    profileVM.logout(context)
                                    nav.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        // 🧁 Detalle producto
                        composable("${Screen.ProductDetail.route}/{id}/{name}/{price}/{imageRes}") { back ->
                            val id = back.arguments?.getString("id") ?: ""
                            val name = Uri.decode(back.arguments?.getString("name") ?: "Producto")
                            val price = back.arguments?.getString("price")?.toIntOrNull() ?: 0
                            val imageInt = back.arguments?.getString("imageRes")?.toIntOrNull()
                            val imageRes: Int? = imageInt?.takeIf { it != -1 }

                            com.example.pasteleriamilsabores.view.catalog.ProductDetailScreen(
                                productName = name,
                                price = price,
                                imageRes = imageRes,
                                onAddToCart = { qty, _ ->
                                    addToCart(id = id, name = name, price = price, qty = qty)
                                    nav.popBackStack()
                                }
                            )
                        }
                    }
                }

                // ---------- UI con Drawer ----------
                if (showDrawer) {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Text(
                                    "Pastelería Mil Sabores",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(16.dp)
                                )

                                NavigationDrawerItem(label = { Text("Catálogo") }, selected = currentRoute == Screen.Catalog.route, onClick = {
                                    nav.navigate(Screen.Catalog.route) { noDupes() }; scope.launch { drawerState.close() }
                                })
                                NavigationDrawerItem(label = { Text("Carrito") }, selected = currentRoute == Screen.Cart.route, onClick = {
                                    nav.navigate(Screen.Cart.route) { noDupes() }; scope.launch { drawerState.close() }
                                })
                                NavigationDrawerItem(label = { Text("Perfil") }, selected = currentRoute == Screen.Profile.route, onClick = {
                                    nav.navigate(Screen.Profile.route) { noDupes() }; scope.launch { drawerState.close() }
                                })

                                HorizontalDivider()

                                NavigationDrawerItem(label = { Text("Cerrar sesión") }, selected = false, onClick = {
                                    nav.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    scope.launch { drawerState.close() }
                                })
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text("Mil Sabores") },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                                        }
                                    }
                                )
                            }
                        ) { padding -> AppNavHost(Modifier.padding(padding)) }
                    }
                } else {
                    Scaffold { padding -> AppNavHost(Modifier.padding(padding)) }
                }
            }
        }
    }
}
