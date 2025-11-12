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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.viewmodel.register.RegisterViewModel
import androidx.core.content.edit // 👈 para usar sp.edit { ... }

@Composable
fun RegisterScreen(
    onDone: () -> Unit,
    onBackToLogin: () -> Unit = {},
    vm: RegisterViewModel = viewModel()
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()                 // 👈 usaremos scope.launch
    val context = LocalContext.current
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
                            // Guardado local
                            saveLocalUser(context, ui.fullName, ui.email, ui.phone)
                            onDone()
                        },
                        onError = { msg ->
                            // ⛔ NO usar LaunchedEffect aquí (no es composable)
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

private fun saveLocalUser(context: Context, name: String, email: String, phone: String) {
    val sp = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
    sp.edit {
        putString("nombre", name)
        putString("correo", email)
        putString("telefono", phone)
    } // apply() implícito con la ext KTX
}
