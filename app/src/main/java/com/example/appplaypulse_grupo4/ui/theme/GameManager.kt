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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameManagerScreen() {
    val gameList = remember { mutableStateListOf<Game>() }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopNavBar(title = "PlayPulse") } //  Llama al mismo nav
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //  Lista de juegos
            GameListScreen(gameList)

            //  Botón flotante abajo a la izquierda
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

            //  Diálogo para agregar juegos
            if (showDialog) {
                AddGameDialog(
                    onDismiss = { showDialog = false },
                    onAddGame = { newGame ->
                        gameList.add(newGame)
                        showDialog = false
                    }
                )
            }
        }
    }
}

//  Datos del juego
data class Game(val name: String, val imageRes: Int)

//  Cuadro para buscar/agregar juego
@Composable
fun AddGameDialog(onDismiss: () -> Unit, onAddGame: (Game) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    //  Base de datos simulada
    val gameDatabase = listOf(
        Game("Apex Legends", R.drawable.apex),
        Game("Magic Arena", R.drawable.arena),
        Game("New World Aeternum", R.drawable.nw),
        Game("Final Fantasy XIV", R.drawable.finalfantasy),
        Game("League of Legends", R.drawable.lol),
        Game("Minecraft", R.drawable.minecraft)
    )

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

                //  Vista previa del juego
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
                        Text(game.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedGame?.let { onAddGame(it) }
                },
                enabled = selectedGame != null
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

//  Lista de juegos guardados
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
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(game.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
