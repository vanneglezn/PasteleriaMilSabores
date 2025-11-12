@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pasteleriamilsabores.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.model.UserProfile
import com.example.pasteleriamilsabores.viewmodel.profile.ProfileViewModel

private val LightPink = Color(0xFFFFD6E7)
private val Lilac = Color(0xFF9B6BFF)
private val DeepLilac = Color(0xFF7A4BFF)
private val CardBg = Color(0xFFFDF7FB)

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val user by vm.user.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }

    // Cargar datos locales al abrir
    LaunchedEffect(Unit) { vm.loadFromPrefs(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi perfil",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightPink)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Avatar",
                tint = Color(0xFFB9B9B9),
                modifier = Modifier.size(108.dp).clip(CircleShape)
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                ProfileRow("Nombre", user.nombre.ifBlank { "—" })
                ProfileRow("Teléfono", user.telefono.ifBlank { "—" })
                ProfileRow("Correo", user.correo.ifBlank { "—" })
                ProfileRow("Edad", user.edad.ifBlank { "—" })
                ProfileRow("Beneficio", user.beneficio.ifBlank { "—" })
            }

            // 🔹 Botón editar
            TextButton(onClick = { showEditDialog = true }) {
                Text("Actualizar información", color = DeepLilac, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lilac, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Cerrar sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    // 🔹 Diálogo para editar
    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onSave = { updated ->
                vm.saveToPrefs(context, updated) // guarda en prefs
                showEditDialog = false
            },
            onCancel = { showEditDialog = false }
        )
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label.lowercase(), color = Color.DarkGray, fontSize = 12.sp)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = LightPink)
    }
}

@Composable
private fun EditProfileDialog(
    user: UserProfile,
    onSave: (UserProfile) -> Unit,
    onCancel: () -> Unit
) {
    var nombre by remember { mutableStateOf(user.nombre) }
    var telefono by remember { mutableStateOf(user.telefono) }
    var correo by remember { mutableStateOf(user.correo) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Editar perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    user.copy(
                        nombre = nombre,
                        telefono = telefono,
                        correo = correo
                    )
                )
            }) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancelar") }
        }
    )
}
