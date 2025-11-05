package com.example.pasteleriamilsabores.navegation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Catalog : Screen("catalog")
    data object Cart : Screen("cart")
    data object Tracking : Screen("tracking")
    data object Profile : Screen("profile")

    data object ProductDetail : Screen("productDetail")

}
