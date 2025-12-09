package com.example.pasteleriamilsabores.view.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.pasteleriamilsabores.viewmodel.login.LoginViewModel
import com.example.pasteleriamilsabores.data.AuthRepository
import com.example.pasteleriamilsabores.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onGoRegister: () -> Unit,
    authRepository: AuthRepository // ⬅ necesario para construir el ViewModel
) {

    val vm: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer { LoginViewModel(authRepository) }
        }
    )

    val ui by vm.uiState.collectAsStateWithLifecycle()
    var showPassword by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFE4EC), Color(0xFFFFC1D6))
                )
            )
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_pasteles),
            contentDescription = "Fondo Pastelería",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.2f)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "MIL SABORES",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = Color(0xFF8E24AA)
            )

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .alpha(0.95f),
                colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.85f)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Iniciar Sesión",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A1B9A)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = ui.email,
                        onValueChange = vm::onEmailChange,
                        label = { Text("Correo electrónico") },
                        isError = ui.emailError != null,
                        supportingText = { ui.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ui.password,
                        onValueChange = vm::onPasswordChange,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = ui.passError != null,
                        supportingText = { ui.passError?.let { Text(it, color = MaterialTheme.colorScheme.error) }},
                        visualTransformation =
                            if (showPassword) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF8E24AA)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions {
                            vm.submitLogin(onSuccess = onLogin)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { vm.submitLogin(onSuccess = onLogin) },
                        enabled = ui.isFormValid && !ui.isLoading,
                        colors = ButtonDefaults.buttonColors(Color(0xFF8E24AA)),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Ingresar", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = onGoRegister) {
                        Text("¿No tienes cuenta? Regístrate aquí", color = Color(0xFF6A1B9A))
                    }
                }
            }
        }
    }
}
