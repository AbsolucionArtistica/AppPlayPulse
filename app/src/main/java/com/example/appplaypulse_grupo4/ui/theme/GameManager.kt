package com.example.appplaypulse_grupo4.ui.theme

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appplaypulse_grupo4.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.first



// ⚙️ DataStore dentro del mismo archivo
// ⚙️ DataStore dentro del mismo archivo
val Context.gameDataStore by preferencesDataStore(name = "games_store")

class GameRepository(private val context: Context) {

    companion object {
        private val GAME_KEY = stringSetPreferencesKey("game_list")
    }

    // 📤 Obtener juegos guardados como flujo (Flow)
    val games: Flow<Set<String>> = context.gameDataStore.data.map { prefs: Preferences ->
        prefs[GAME_KEY] ?: emptySet()
    }

    // 💾 Agregar un nuevo juego
    suspend fun addGame(game: String) {
        context.gameDataStore.edit { prefs ->
            val current = prefs[GAME_KEY] ?: emptySet()
            prefs[GAME_KEY] = current + game // usamos el operador + para agregar un nuevo elemento al Set
        }
    }
}


// 🎮 Datos del juego
data class Game(val name: String, val imageRes: Int)

// 🧱 Base de juegos simulada
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
fun GameManagerScreen() {
    val context = LocalContext.current
    val repo = remember { GameRepository(context) }
    val scope = rememberCoroutineScope()

    // 🔄 Leer juegos guardados desde DataStore
    val savedGames by repo.games.collectAsState(initial = emptySet())

    // 📋 Convertir nombres a objetos Game
    val gameList = remember(savedGames) {
        savedGames.mapNotNull { name ->
            gameDatabase.find { it.name == name }
        }.toMutableList()
    }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopNavBar(title = "PlayPulse") }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GameListScreen(gameList)

            // ➕ Botón flotante
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
                    onAddGame = { newGame ->
                        scope.launch {
                            repo.addGame(newGame.name) // 💾 Guarda el nombre del juego
                        }
                        showDialog = false
                    }
                )
            }
        }
    }
}

// 🧾 Lista visual de juegos agregados
@Composable
fun GameListScreen(gameList: List<Game>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tus juegos:", style = MaterialTheme.typography.titleMedium)

        if (gameList.isEmpty()) {
            Text("No hay juegos agregados aún.")
        } else {
            gameList.forEach { game ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = game.imageRes),
                        contentDescription = game.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(game.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// 🪟 Cuadro para buscar/agregar juego
@Composable
fun AddGameDialog(onDismiss: () -> Unit, onAddGame: (Game) -> Unit) {
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

                // 👁️ Vista previa del juego encontrado
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
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
