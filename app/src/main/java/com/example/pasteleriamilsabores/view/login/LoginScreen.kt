package com.example.pasteleriamilsabores.view.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.viewmodel.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onGoRegister: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val ui by vm.uiState.collectAsStateWithLifecycle()
    var showPassword by remember { mutableStateOf(false) }

    // Snackbar opcional para mensajes del VM
    val snackHost = remember { SnackbarHostState() }
    LaunchedEffect(ui.message) {
        ui.message?.let {
            snackHost.showSnackbar(it)
            vm.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackHost) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra superior con color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFB3C1),
                                Color(0xFFFF80AB)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍰 Mil Sabores",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(Modifier.height(40.dp))

            // Frase
            Text(
                text = "🍪 ENDULZA TU DÍA CON EL SABOR DE NUESTROS POSTRES 🍓",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFAD1457),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(30.dp))

            Text("Iniciar Sesión", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(24.dp))

            // Email
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = ui.emailError != null,
                supportingText = {
                    ui.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(Modifier.height(12.dp))

            // Contraseña
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = ui.passError != null,
                supportingText = {
                    ui.passError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color(0xFF8E24AA)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        vm.submitLogin(
                            onSuccess = onLogin,
                            onError = { msg -> /* ya se muestra en snackbar */ }
                        )
                    }
                ),
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(Modifier.height(28.dp))

            // Botón
            Button(
                onClick = {
                    vm.submitLogin(
                        onSuccess = onLogin,
                        onError = { /* ya hay snackbar */ }
                    )
                },
                enabled = ui.isFormValid && !ui.isLoading,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (ui.isFormValid && !ui.isLoading)
                        Color(0xFF8E24AA) else Color(0xFFCE93D8),
                    disabledContainerColor = Color(0xFFCE93D8)
                )
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Ingresar", fontSize = 16.sp, color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onGoRegister, enabled = !ui.isLoading) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color(0xFF8E24AA))
            }
        }
    }
}
