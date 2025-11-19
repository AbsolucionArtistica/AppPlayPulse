package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onClose: (() -> Unit)? = null,
    onLoginSuccess: (() -> Unit)? = null,
    onRegisterSuccess: (() -> Unit)? = null,
    onGoogleAuth: (() -> Unit)? = null,
    googleError: String? = null
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Iniciar sesión, 1 = Registrarse
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(googleError) {
        if (!googleError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(googleError)
        }
    }

    Scaffold(
        topBar = { TopNavBar(if (selectedTab == 0) "Iniciar sesión" else "Registrarse") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Pestañas
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Iniciar sesión") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Registrarse") })
            }

            when (selectedTab) {
                0 -> LoginForm(
                    onForgot = { /* abre diálogo en el form */ },
                    onLogin = { userOrEmailOrPhone, password ->
                        if (userOrEmailOrPhone.isNotBlank() && password.isNotBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("¡Inicio de sesión exitoso!") }
                            onLoginSuccess?.invoke()
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Completa usuario/correo/teléfono y contraseña") }
                        }
                    },
                    onGoogleAuth = onGoogleAuth
                )
                1 -> RegisterForm(
                    onRegister = { nombre, apellido, edad, correo, telefono, username, password ->
                        val errors = validateRegister(
                            nombre, apellido, edad, correo, telefono, username, password
                        )
                        if (errors.isEmpty()) {
                            scope.launch { snackbarHostState.showSnackbar("¡Registro exitoso!") }
                            onRegisterSuccess?.invoke()
                        } else {
                            scope.launch { snackbarHostState.showSnackbar(errors.first()) }
                        }
                    },
                    onGoogleAuth = onGoogleAuth
                )
            }
        }
    }
}

/* -------------------- LOGIN -------------------- */

@Composable
private fun LoginForm(
    onForgot: () -> Unit,
    onLogin: (String, String) -> Unit,
    onGoogleAuth: (() -> Unit)?
) {
    var userField by remember { mutableStateOf("") } // usuario/correo/teléfono
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotTarget by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        OutlinedTextField(
            value = userField,
            onValueChange = { userField = it },
            label = { Text("Usuario, correo o teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Ocultar" else "Mostrar")
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { showForgotDialog = true }) {
                Text("¿Olvidaste tu contraseña?")
            }
            Button(onClick = { onLogin(userField, password) }) {
                Text("Ingresar")
            }
        }

        OutlinedButton(
            onClick = { onGoogleAuth?.invoke() },
            modifier = Modifier.fillMaxWidth(),
            enabled = onGoogleAuth != null
        ) {
            Text("Continuar con Google")
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Recuperar contraseña") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingresa tu correo o usuario para enviarte un enlace de recuperación.")
                    OutlinedTextField(
                        value = forgotTarget,
                        onValueChange = { forgotTarget = it },
                        label = { Text("Correo o usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Aquí puedes integrar tu lógica real de recuperación
                    showForgotDialog = false
                }) { Text("Enviar") }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

/* -------------------- REGISTRO -------------------- */

@Composable
private fun RegisterForm(
    onRegister: (String, String, String, String, String, String, String) -> Unit,
    onGoogleAuth: (() -> Unit)?
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val passwordErrors = remember(password) { passwordValidationErrors(password) }
    val isPasswordValid = passwordErrors.isEmpty()

    val scroll = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)      // scroll
            .imePadding()                // sube con el teclado
            .navigationBarsPadding()     // evita solaparse con barra
            .padding(bottom = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = edad,
            onValueChange = { if (it.all { ch -> ch.isDigit() } && it.length <= 3) edad = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { if (it.all { ch -> ch.isDigit() } && it.length <= 15) telefono = it },
            label = { Text("Número de teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Ocultar" else "Mostrar")
                }
            },
            supportingText = {
                if (!isPasswordValid && password.isNotEmpty()) {
                    Column { passwordErrors.forEach { Text("• $it") } }
                } else {
                    Text("Mín. 6 caracteres, mayúscula, minúscula y número")
                }
            }
        )

        val canRegister = nombre.isNotBlank() &&
                apellido.isNotBlank() &&
                edad.isNotBlank() &&
                correo.isNotBlank() &&
                telefono.isNotBlank() &&
                username.isNotBlank() &&
                isPasswordValid

        Button(
            onClick = { onRegister(nombre, apellido, edad, correo, telefono, username, password) },
            enabled = canRegister,
            modifier = Modifier.align(Alignment.End)
        ) { Text("Crear cuenta") }

        OutlinedButton(
            onClick = { onGoogleAuth?.invoke() },
            modifier = Modifier.fillMaxWidth(),
            enabled = onGoogleAuth != null
        ) { Text("Registrarte con Google") }
    }
}

/* -------------------- VALIDACIONES -------------------- */

private fun validateRegister(
    nombre: String,
    apellido: String,
    edad: String,
    correo: String,
    telefono: String,
    username: String,
    password: String
): List<String> {
    val errors = mutableListOf<String>()

    if (nombre.isBlank()) errors += "Ingresa tu nombre"
    if (apellido.isBlank()) errors += "Ingresa tu apellido"
    if (edad.isBlank()) errors += "Ingresa tu edad"
    else {
        val e = edad.toIntOrNull()
        if (e == null) errors += "Edad inválida"
        else if (e < 13) errors += "Debes tener al menos 13 años"
    }

    if (!correo.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")))
        errors += "Correo inválido"

    if (!telefono.matches(Regex("^\\d{7,15}\$")))
        errors += "Teléfono inválido (7 a 15 dígitos)"

    if (username.length < 3)
        errors += "Usuario muy corto (mín. 3)"

    errors += passwordValidationErrors(password)

    return errors
}

private fun passwordValidationErrors(password: String): List<String> {
    val errs = mutableListOf<String>()
    if (password.length < 6) errs += "Debe tener al menos 6 caracteres"
    if (!password.any { it.isUpperCase() }) errs += "Debe incluir una mayúscula"
    if (!password.any { it.isLowerCase() }) errs += "Debe incluir una minúscula"
    if (!password.any { it.isDigit() }) errs += "Debe incluir un número"
    return errs
}
