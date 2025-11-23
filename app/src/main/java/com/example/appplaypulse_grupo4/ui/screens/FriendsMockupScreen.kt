package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.ui.theme.Friend // modelo usado en HomeScreen
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsMockupScreen(
    currentUsername: String?,
    friends: List<Friend>,          // amigos ya guardados en BD para este usuario
    suggestedUsers: List<User>,     // usuarios REALES que se pueden agregar
    onClose: () -> Unit,
    onAddFriendToDb: (User) -> Unit // al confirmar, se guarda en BD
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopNavBar(title = "Amigos") }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Lista visual de amigos (los que vienen de la BD)
            FriendList(friendList = friends)

            // Botón flotante para agregar
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", color = MaterialTheme.colorScheme.onPrimary)
            }

            // Diálogo para agregar amigos
            if (showDialog) {
                AddFriendDialog(
                    currentUsername = currentUsername,
                    suggestedUsers = suggestedUsers,
                    onDismiss = { showDialog = false },
                    onAddFriend = { user ->
                        onAddFriendToDb(user)
                        showDialog = false
                    }
                )
            }
        }
    }
}

/* ============= AVATAR CON INICIALES ============= */

@Composable
fun InitialsAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val initials = remember(name) {
        name.trim()
            .split(" ", "_")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/* ============= DIÁLOGO: AGREGAR AMIGO ============= */

@Composable
fun AddFriendDialog(
    currentUsername: String?,
    suggestedUsers: List<User>,
    onDismiss: () -> Unit,
    onAddFriend: (User) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFriend by remember { mutableStateOf<User?>(null) }

    // Filtrar usuarios sugeridos según lo que escribe
    val filteredUsers by remember(searchQuery, suggestedUsers) {
        mutableStateOf(
            suggestedUsers
                .filter { it.username != currentUsername } // nunca tú misma
                .filter { it.username.contains(searchQuery, ignoreCase = true) }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar amigo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        selectedFriend = filteredUsers.firstOrNull()
                    },
                    label = { Text("Buscar amigo por nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                selectedFriend?.let { user ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InitialsAvatar(
                            name = user.username,
                            size = 72.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedFriend?.let(onAddFriend) },
                enabled = selectedFriend != null
            ) {
                Text("Enviar solicitud")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

/* ============= LISTA DE AMIGOS ============= */

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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        // Avatar con iniciales
                        InitialsAvatar(
                            name = friend.name,
                            size = 60.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(friend.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${friend.gameName} • ${friend.hours}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
