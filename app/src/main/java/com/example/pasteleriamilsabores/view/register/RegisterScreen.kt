@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pasteleriamilsabores.view.register

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// 💡 NUEVOS IMPORTS PARA LA INYECCIÓN
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pasteleriamilsabores.data.AuthRepository // Importar Repositorio de Autenticación
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.viewmodel.register.RegisterViewModel
// import androidx.core.content.edit // ❌ ELIMINADO: No se usa SharedPreferences


@Composable
fun RegisterScreen(
    onDone: () -> Unit,
    onBackToLogin: () -> Unit = {},
    // 💡 CAMBIO 1: RECIBIR EL REPOSITORIO COMO DEPENDENCIA
    authRepository: AuthRepository
) {
    // 💡 CAMBIO 2: FACTORY PARA INYECTAR LA DEPENDENCIA
    val vm: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RegisterViewModel(authRepository)
            }
        }
    )

    val ui by vm.ui.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Contexto sigue siendo necesario para algunos casos (Snackbars/etc)
    var showPass by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Crear cuenta nueva", fontWeight = FontWeight.Bold, fontSize = 20.sp) })
        },
        snackbarHost = { SnackbarHost(snack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Completa tus datos para registrarte",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = ui.fullName,
                onValueChange = vm::onNameChange,
                label = { Text("Nombre completo") },
                isError = ui.errors.containsKey("fullName"),
                supportingText = { ui.errors["fullName"]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.phone,
                onValueChange = vm::onPhoneChange,
                label = { Text("Celular") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = ui.errors.containsKey("phone"),
                supportingText = { ui.errors["phone"]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = ui.errors.containsKey("email"),
                supportingText = { ui.errors["email"]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                isError = ui.errors.containsKey("password"),
                supportingText = { ui.errors["password"]?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.promoCode,
                onValueChange = vm::onPromoChange,
                label = { Text("Código promocional (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    vm.submit(
                        onSuccess = {
                            // 💡 CAMBIO 3: ELIMINAR LLAMADA AL GUARDADO LOCAL (Room lo hace vía VM)
                            // saveLocalUser(context, ui.fullName, ui.email, ui.phone) // <-- ELIMINADO
                            onDone()
                        },
                        onError = { msg ->
                            scope.launch { snack.showSnackbar(msg) }
                        }
                    )
                },
                enabled = ui.canSubmit && !ui.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (ui.isLoading)
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                else
                    Text("Registrarse")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("¿Ya tienes cuenta? Inicia sesión") }
        }
    }
}

