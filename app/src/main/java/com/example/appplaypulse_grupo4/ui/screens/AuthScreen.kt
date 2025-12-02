package com.example.appplaypulse_grupo4.ui.screens

import android.content.Intent
import android.util.Log
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.validation.FormValidators
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLogin: (field: String, password: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onRegister: (
        nombre: String,
        apellido: String,
        edad: String,
        correo: String,
        telefono: String,
        username: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) -> Unit,
    onRegisterWithGoogle: (name: String, email: String, onResult: (Boolean, String) -> Unit) -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Google Sign-In client
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
    val googleSignInClient: GoogleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Campos compartidos
    var field by remember { mutableStateOf("") }      // username / email / telefono para login
    var password by remember { mutableStateOf("") }

    // Visibilidad de Contrasena
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var registerPasswordVisible by remember { mutableStateOf(false) }

    // Campos registro
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    var message by remember { mutableStateOf<String?>(null) }
    var isGoogleLoading by remember { mutableStateOf(false) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        scope.launch {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.result
                val email = account?.email
                val displayName = account?.displayName ?: account?.givenName
                if (email.isNullOrBlank()) {
                    message = "No se pudo obtener el correo de Google"
                } else {
                    isGoogleLoading = true
                    onRegisterWithGoogle(displayName ?: "Jugador Google", email) { _, msg ->
                        message = msg
                        isGoogleLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthScreen", "Google sign-in error", e)
                message = "Error al iniciar con Google"
                isGoogleLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLoginMode) "Iniciar sesion" else "Crear cuenta",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(16.dp))

        if (isLoginMode) {
            // ---------- LOGIN ----------
            OutlinedTextField(
                value = field,
                onValueChange = { field = it },
                label = { Text("Usuario / Email / Telefono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasena") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (loginPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (loginPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val desc =
                        if (loginPasswordVisible) "Ocultar Contrasena" else "Mostrar Contrasena"

                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = desc)
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val error =
                        FormValidators.validateRequired(field, "Usuario / Email / Telefono")
                            ?: FormValidators.validateRequired(password, "Contrasena")
                    if (error != null) {
                        message = error
                        return@Button
                    }

                    onLogin(field.trim(), password.trim()) { ok, msg ->
                        message = msg
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    isGoogleLoading = true
                    googleLauncher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGoogleLoading
            ) {
                if (isGoogleLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (isGoogleLoading) "Conectando..." else "Crear cuenta con Google")
            }
        } else {
            // ---------- REGISTRO ----------
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Telefono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasena") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (registerPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (registerPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val desc =
                        if (registerPasswordVisible) "Ocultar Contrasena" else "Mostrar Contrasena"

                    IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = desc)
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val validationError =
                        FormValidators.validateName(nombre, "Nombre")
                            ?: FormValidators.validateName(apellido, "Apellido")
                            ?: FormValidators.validateAge(edad)
                            ?: FormValidators.validateEmail(correo)
                            ?: FormValidators.validatePhone(telefono)
                            ?: FormValidators.validateUsername(username)
                            ?: FormValidators.validatePassword(password)

                    if (validationError != null) {
                        message = validationError
                        return@Button
                    }

                    onRegister(
                        nombre.trim(),
                        apellido.trim(),
                        edad.trim(),
                        correo.trim(),
                        telefono.trim(),
                        username.trim(),
                        password.trim()
                    ) { ok, msg ->
                        message = msg
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = {
            isLoginMode = !isLoginMode
            // opcional: limpiar mensaje al cambiar
            message = null
        }) {
            Text(
                text = if (isLoginMode)
                    "¿No tienes cuenta? Registrate"
                else
                    "¿Ya tienes cuenta? Inicia sesion"
            )
        }

        message?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it)
        }
    }
}
