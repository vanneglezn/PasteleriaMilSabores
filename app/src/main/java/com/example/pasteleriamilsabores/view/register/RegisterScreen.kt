package com.example.pasteleriamilsabores.view.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions   // ✅ import correcto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType       // ✅ este sí es de ui.text.input
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RegisterScreen(
    onDone: () -> Unit,
    onBackToLogin: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var promo by remember { mutableStateOf("") }

    // Validaciones simples
    val isEmailValid = email.contains("@") && email.contains(".")
    val isPasswordValid = pass.length >= 6
    val isFormValid = name.isNotBlank() && phone.isNotBlank() && isEmailValid && isPasswordValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crear cuenta nueva",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            )
        }
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Celular") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = email.isNotBlank() && !isEmailValid,
                supportingText = {
                    if (email.isNotBlank() && !isEmailValid) Text("Formato de correo inválido")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = pass.isNotBlank() && !isPasswordValid,
                supportingText = {
                    if (pass.isNotBlank() && !isPasswordValid)
                        Text("Debe tener al menos 6 caracteres")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = promo,
                onValueChange = { promo = it },
                label = { Text("Código promocional (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onDone,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
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

