@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pasteleriamilsabores.view.register

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import kotlinx.coroutines.launch

import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.pasteleriamilsabores.data.AuthRepository
import com.example.pasteleriamilsabores.viewmodel.register.RegisterViewModel

@Composable
fun RegisterScreen(
    onDone: () -> Unit,
    onBackToLogin: () -> Unit,
    authRepository: AuthRepository // SE MANTIENE DEPENDENCIA
) {
    val vm: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer { RegisterViewModel(authRepository) }
        }
    )

    val ui by vm.ui.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showPass by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crear Cuenta",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
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
                "Completa tus datos para continuar",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.fullName,
                onValueChange = vm::onNameChange,
                label = { Text("Nombre Completo") },
                isError = ui.errors["fullName"] != null,
                supportingText = { ui.errors["fullName"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.phone,
                onValueChange = vm::onPhoneChange,
                label = { Text("Número Celular") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = ui.errors["phone"] != null,
                supportingText = { ui.errors["phone"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = ui.errors["email"] != null,
                supportingText = { ui.errors["email"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = ui.errors["password"] != null,
                supportingText = { ui.errors["password"]?.let { Text(it) } },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/ocultar"
                        )
                    }
                },
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

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    vm.submit(
                        onSuccess = { onDone() },
                        onError = { scope.launch { snack.showSnackbar(it) } }
                    )
                },
                enabled = ui.canSubmit && !ui.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (ui.isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp))
                else Text("Registrarme")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Ya tengo cuenta")
            }
        }
    }
}
