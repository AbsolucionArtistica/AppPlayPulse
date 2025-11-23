package com.example.appplaypulse_grupo4.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.database.AppDatabase
import com.example.appplaypulse_grupo4.database.entity.UserGameEntity
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar
import kotlinx.coroutines.launch

// 🎮 Modelo simple de juego para elegir en el diálogo
data class Game(val name: String, val imageRes: Int)

// 🧱 Base de juegos disponibles
val gameDatabase = listOf(
    Game("Apex Legends", R.drawable.apex),
    Game("Magic Arena", R.drawable.arena),
    Game("New World Aeternum", R.drawable.nw),
    Game("Final Fantasy XIV", R.drawable.finalfantasy),
    Game("League of Legends", R.drawable.lol),
    Game("Minecraft", R.drawable.minecraft)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameManager(
    db: AppDatabase,
    currentUserId: Long?,
    onGameAdded: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var userGames by remember { mutableStateOf<List<UserGameEntity>>(emptyList()) }

    // 🔄 Cada vez que cambie el usuario, recargamos sus juegos
    LaunchedEffect(currentUserId) {
        val uid = currentUserId
        userGames = if (uid != null) {
            db.userGameDao().getRecentGamesForUser(uid)
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = { TopNavBar(title = "Juegos") }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // 📋 Lista de juegos del usuario
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tus juegos",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                if (userGames.isEmpty()) {
                    Text("Todavía no has agregado juegos.")
                } else {
                    userGames.forEach { ug ->
                        val imgRes = context.resources.getIdentifier(
                            ug.imageResName,   // 👈 ahora sí es el nombre del drawable
                            "drawable",
                            context.packageName
                        ).takeIf { it != 0 } ?: R.drawable.apex

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = imgRes),
                                contentDescription = ug.gameTitle,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(ug.gameTitle, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            // ➕ FAB para agregar juego
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar juego",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // 🪟 Diálogo de agregar juego
            if (showDialog) {
                AddGameDialog(
                    onDismiss = { showDialog = false },
                    onAddGame = { selected ->
                        val uid = currentUserId
                        if (uid != null) {
                            scope.launch {
                                // 👇 Guardamos el NOMBRE del drawable, no el número
                                val resName = context.resources.getResourceEntryName(selected.imageRes)

                                db.userGameDao().insertUserGame(
                                    UserGameEntity(
                                        userId = uid,
                                        gameTitle = selected.name,
                                        imageResName = resName
                                    )
                                )

                                // Recargamos lista local
                                userGames = db.userGameDao().getRecentGamesForUser(uid)

                                // Avisamos al MainActivity para actualizar el perfil
                                onGameAdded()
                            }
                        }
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onAddGame: (Game) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar juego") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        selectedGame = gameDatabase.find {
                            it.name.contains(query, ignoreCase = true)
                        }
                    },
                    label = { Text("Buscar juego") },
                    modifier = Modifier.fillMaxWidth()
                )

                selectedGame?.let { game ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = game.imageRes),
                            contentDescription = game.name,
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(game.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedGame?.let { onAddGame(it) } },
                enabled = selectedGame != null
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
