package com.example.appplaypulse_grupo4.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// ⚙️ DataStore configurado en el mismo archivo
val Context.friendDataStore by preferencesDataStore(name = "friends_store")

class FriendRepository(private val context: Context) {
    companion object {
        private val FRIEND_KEY = stringSetPreferencesKey("friend_list")
    }

    // 🔄 Obtener amigos guardados
    val friends: Flow<Set<String>> = context.friendDataStore.data.map { prefs: Preferences ->
        prefs[FRIEND_KEY] ?: emptySet()
    }

    // 💾 Guardar nuevo amigo
    suspend fun addFriend(friend: String) {
        context.friendDataStore.edit { prefs ->
            val current = prefs[FRIEND_KEY] ?: emptySet()
            prefs[FRIEND_KEY] = current + friend
        }
    }

    // ❌ Eliminar amigo
    suspend fun removeFriend(friend: String) {
        context.friendDataStore.edit { prefs ->
            val current = prefs[FRIEND_KEY] ?: emptySet()
            prefs[FRIEND_KEY] = current - friend
        }
    }
}

// 👥 Clase Friend (nombre + imagen + estado)
data class Friend(
    val name: String,
    val profileRes: Int,
    val isOnline: Boolean = listOf(true, false).random()
)

// 📚 Base simulada de amigos (puedes editarla libremente)
val friendDatabase = listOf(
    Friend("Nuggw", R.drawable.giphy),
    Friend("Raygimon21", R.drawable.agua),
    Friend("Ferna_nda_k", R.drawable.elena),
    Friend("Eth3rn4l", R.drawable.nw)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsMockupScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { FriendRepository(context) }
    val scope = rememberCoroutineScope()

    // 🔄 Leer los amigos guardados
    val savedFriends by repo.friends.collectAsState(initial = emptySet())

    // 📋 Convertir los nombres a objetos Friend
    val friendList = remember(savedFriends) {
        savedFriends.mapNotNull { name ->
            friendDatabase.find { it.name == name }
        }.toMutableStateList()
    }

    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopNavBar(title = "Amigos") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 📋 Lista de amigos guardados
            FriendList(friendList) { friend ->
                scope.launch {
                    repo.removeFriend(friend.name)
                }
            }

            // ➕ Botón flotante para agregar
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", color = MaterialTheme.colorScheme.onPrimary)
            }

            // 🪟 Diálogo para agregar amigos
            if (showDialog) {
                AddFriendDialog(
                    onDismiss = { showDialog = false },
                    onAddFriend = { newFriend ->
                        scope.launch {
                            repo.addFriend(newFriend.name)
                        }
                        showDialog = false
                    }
                )
            }

            // ✅ Snackbar al agregar amigo
            LaunchedEffect(friendList.size) {
                if (friendList.isNotEmpty()) {
                    val lastFriend = friendList.last()
                    snackbarHostState.showSnackbar("Solicitud enviada a ${lastFriend.name}")
                }
            }
        }
    }
}

// 🪟 Diálogo para buscar y agregar amigo
@Composable
fun AddFriendDialog(onDismiss: () -> Unit, onAddFriend: (Friend) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFriend by remember { mutableStateOf<Friend?>(null) }

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
fun FriendList(friendList: List<Friend>, onRemoveFriend: (Friend) -> Unit) {
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

                        Column {
                            Text(friend.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                if (friend.isOnline) "🟢 En línea" else "⚫ Desconectado",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // 🗑️ Botón eliminar (guarda el cambio)
                        IconButton(onClick = { onRemoveFriend(friend) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar amigo",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
