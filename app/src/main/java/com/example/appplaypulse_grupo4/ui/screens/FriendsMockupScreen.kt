package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar // ✅ Importa tu nav personalizado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsMockupScreen(onClose: () -> Unit) {
    val friendList = remember { mutableStateListOf<Friend>() }
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            // ✅ Usa el mismo TopNavBar que en Home y GameManager
            TopNavBar(title = "Amigos")
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 📋 Lista de amigos
            FriendList(friendList)

            // ➕ Botón flotante
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", color = MaterialTheme.colorScheme.onPrimary)
            }

            // 🪟 Ventana para agregar amigos
            if (showDialog) {
                AddFriendDialog(
                    onDismiss = { showDialog = false },
                    onAddFriend = { newFriend ->
                        friendList.add(newFriend)
                        showDialog = false
                    }
                )
            }

            // ✅ Mostrar Snackbar en contexto @Composable
            LaunchedEffect(friendList.size) {
                if (friendList.isNotEmpty()) {
                    val lastFriend = friendList.last()
                    snackbarHostState.showSnackbar("Solicitud enviada a ${lastFriend.name}")
                }
            }
        }
    }
}

// 👥 Clase Friend (solo nombre + imagen)
data class Friend(
    val name: String,
    val profileRes: Int,
    val isOnline: Boolean = listOf(true, false).random()
)

// 🪟 Diálogo para buscar y agregar amigo
@Composable
fun AddFriendDialog(onDismiss: () -> Unit, onAddFriend: (Friend) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFriend by remember { mutableStateOf<Friend?>(null) }

    // 📚 Base simulada de amigos (usa tus imágenes locales)
    val friendDatabase = listOf(
        Friend("Nuggw", R.drawable.giphy),
        Friend("Raygimon21", R.drawable.agua),
        Friend("Ferna_nda_k", R.drawable.elena)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar amigo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        selectedFriend = friendDatabase.find {
                            it.name.contains(query, ignoreCase = true)
                        }
                    },
                    label = { Text("Buscar amigo por nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 👁️ Vista previa del amigo encontrado
                selectedFriend?.let { friend ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = friend.profileRes),
                            contentDescription = friend.name,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(friend.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedFriend?.let { onAddFriend(it) } },
                enabled = selectedFriend != null
            ) { Text("Enviar solicitud") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// 📋 Lista visual de amigos agregados
@Composable
fun FriendList(friendList: List<Friend>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tus amigos:", style = MaterialTheme.typography.titleMedium)

        if (friendList.isEmpty()) {
            Text("Aún no has agregado amigos.")
        } else {
            friendList.forEach { friend ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = friend.profileRes),
                        contentDescription = friend.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(friend.name, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    // 🟢 Estado conectado/desconectado
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (friend.isOnline)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }
        }
    }
}
