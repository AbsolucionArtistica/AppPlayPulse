package com.example.appplaypulse_grupo4.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.api.backend.RemoteBackendDataSource
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameManager(
    backendDataSource: RemoteBackendDataSource,
    remoteUserId: String?,
    onGameAdded: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var userGames by remember { mutableStateOf<List<RecentGame>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(remoteUserId) {
        if (remoteUserId != null) {
            loading = true
            backendDataSource.fetchGames(remoteUserId)
                .onSuccess { games ->
                    userGames = games.map { g ->
                        val res = ctx.resources.getIdentifier(
                            g.title,
                            "drawable",
                            ctx.packageName
                        ).takeIf { it != 0 } ?: R.drawable.apex
                        g.copy(imageRes = res)
                    }
                }
            loading = false
        } else {
            userGames = emptyList()
        }
    }

    Scaffold(
        topBar = { TopNavBar(title = "Juegos") },
        floatingActionButton = {
            if (remoteUserId != null) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar juego"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tus juegos:",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (userGames.isEmpty()) {
                Text(
                    text = "Aun no has agregado juegos.\nUsa el boton + para anadir uno.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userGames) { game ->
                        GameRow(
                            gameTitle = game.title,
                            imageRes = game.imageRes
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog && remoteUserId != null) {
        var gameName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Agregar juego") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gameName,
                        onValueChange = { gameName = it },
                        label = { Text("Nombre del juego") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val suggestions = remember(gameName) {
                        if (gameName.isBlank()) {
                            emptyList()
                        } else {
                            knownGames.filter { it.contains(gameName, ignoreCase = true) }
                        }
                    }

                    if (suggestions.isNotEmpty()) {
                        Text(
                            text = "Sugerencias:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        suggestions.forEach { suggestion ->
                            TextButton(
                                onClick = { gameName = suggestion },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(suggestion)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cleanName = gameName.trim()
                        val allowed = Regex("^[A-Za-z0-9 .,'-]{3,40}$")
                        if (cleanName.isBlank()) {
                            Toast.makeText(
                                ctx,
                                "Escribe el nombre del juego",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (!allowed.matches(cleanName)) {
                            Toast.makeText(
                                ctx,
                                "El nombre solo admite letras, numeros y espacios (3-40)",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            scope.launch {
                                val (finalTitle, imageResName) = mapGame(cleanName)
                                val rid = remoteUserId
                                if (rid != null) {
                                    backendDataSource.addGame(
                                        userId = rid,
                                        gameTitle = finalTitle,
                                        imageResName = imageResName
                                    ).onSuccess {
                                        backendDataSource.fetchGames(rid)
                                            .onSuccess { games ->
                                                userGames = games.map { g ->
                                                    val res = ctx.resources.getIdentifier(
                                                        imageResName,
                                                        "drawable",
                                                        ctx.packageName
                                                    ).takeIf { it != 0 } ?: R.drawable.apex
                                                    g.copy(imageRes = res)
                                                }
                                            }
                                    }
                                }
                                onGameAdded()
                            }
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun GameRow(
    gameTitle: String,
    imageRes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = gameTitle,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gameTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Jugado recientemente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val knownGames = listOf(
    "Apex Legends",
    "Final Fantasy XIV",
    "League of Legends",
    "Magic Arena",
    "Minecraft",
    "New World Aeternum"
)

fun mapGame(rawName: String): Pair<String, String> {
    val n = rawName.trim().lowercase()
    return when {
        "apex" in n -> "Apex Legends" to "apex"
        "final" in n || "ffxiv" in n || "fantasy" in n -> "Final Fantasy XIV" to "finalfantasy"
        "league" in n || "lol" in n -> "League of Legends" to "lol"
        "arena" in n || "magic" in n -> "Magic Arena" to "arena"
        "minecraft" in n -> "Minecraft" to "minecraft"
        "new world" in n || "aeternum" in n -> "New World Aeternum" to "nw"
        else -> rawName.trim() to "apex"
    }
}
