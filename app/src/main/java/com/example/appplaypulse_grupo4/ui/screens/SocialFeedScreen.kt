package com.example.appplaypulse_grupo4.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.database.dto.FeedItem
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar
import androidx.compose.material3.TextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    currentUsername: String,
    hasFriends: Boolean,
    posts: List<FeedItem>,
    onPublishPost: (String, String?, String?) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToFriends: () -> Unit
) {
    val context = LocalContext.current

    var text by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    // Guardamos la imagen seleccionada por si después quieres mostrarla
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    // Launcher para abrir la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImage = uri
            Toast.makeText(context, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { TopNavBar(title = "Comunidad") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Saludo
            Text(
                text = "Hola, $currentUsername",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            // ====== Composer tipo X ======
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fila con avatar + texto "¿Qué está pasando?"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Avatar con iniciales (reutiliza el de amigos)
                        InitialsAvatar(
                            name = currentUsername,
                            size = 40.dp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = { Text("¿Qué está pasando?") },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 56.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                    }

                    // "Cualquier persona puede responder"
                    Text(
                        text = "Cualquier persona puede responder",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Divider()

                    // Fila de iconos + botón Publicar (como abajo en X)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 📷 Imagen → abre galería
                            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                                Icon(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = "Agregar imagen"
                                )
                            }

                            // 📍 Ubicación (placeholder)
                            IconButton(onClick = {
                                Toast.makeText(
                                    context,
                                    "Ubicación opcional (pendiente)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Agregar ubicación"
                                )
                            }

                            // Podrías agregar más íconos aquí (GIF, encuesta, emojis…)
                        }

                        Button(
                            onClick = {
                                if (text.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Escribe algo para publicar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    onPublishPost(
                                        text.trim(),
                                        location.takeIf { it.isNotBlank() },
                                        link.takeIf { it.isNotBlank() }
                                    )
                                    text = ""
                                    location = ""
                                    link = ""
                                    selectedImage = null
                                }
                            },
                            enabled = text.isNotBlank(),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("Postear")
                        }
                    }

                    // Campos opcionales debajo (como tus versiones anteriores)
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Ubicación (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Link (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ====== Feed ======
            if (posts.isEmpty()) {
                Text(
                    text = "Aún no hay actividad en tu comunidad.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = if (hasFriends)
                        "Cuando tus amigos publiquen algo, aparecerá aquí."
                    else
                        "Agrega amigos para ver sus publicaciones aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Actividad reciente",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    posts.forEach { post ->
                        PostCard(item = post)
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(item: FeedItem) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Usuario
            Text(
                text = "@${item.username}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            // Contenido
            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // Ubicación opcional
            item.location?.let { loc ->
                if (loc.isNotBlank()) {
                    Text(
                        text = "📍 $loc",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Link opcional (abre navegador)
            item.link?.let { url ->
                if (url.isNotBlank()) {
                    TextButton(
                        onClick = {
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    "No se pudo abrir el enlace",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = url,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Acciones (futuro: likes/respuestas)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* TODO: like */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Like")
                }
                TextButton(onClick = { /* TODO: responder */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Responder")
                }
            }
        }
    }
}
