package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar

// ====== Modelo de datos ======
data class TrophyCounts(val platinum: Int, val gold: Int, val silver: Int, val bronze: Int)
data class RecentGame(val title: String, val imageRes: Int)

data class ProfileData(
    val displayName: String,
    val handle: String,
    val avatarRes: Int?,      // drawable opcional
    val avatarUri: String?,   // foto desde galería
    val trophies: TrophyCounts,
    val recentGames: List<RecentGame>,
    val friendsCount: Int,
    val gamesCount: Int,
    val achievementsCount: Int
)

// ====== Pantalla principal ======
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onClose: () -> Unit,
    data: ProfileData,
    onChangeUsername: (String) -> Unit,
    onChangeAvatarFromGallery: () -> Unit,
    onLogout: () -> Unit
) {
    val isNewUser = remember(data) {
        data.trophies.platinum == 0 &&
                data.trophies.gold == 0 &&
                data.trophies.silver == 0 &&
                data.trophies.bronze == 0 &&
                data.recentGames.isEmpty()
    }

    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopNavBar(title = "Perfil") }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== AVATAR =====
            item {
                when {
                    data.avatarUri != null -> {
                        // Imagen desde galería
                        Image(
                            painter = rememberAsyncImagePainter(data.avatarUri),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    data.avatarRes != null -> {
                        // Drawable del proyecto
                        Image(
                            painter = painterResource(id = data.avatarRes),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        // Avatar vacío / placeholder
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data.displayName.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Botón cambiar foto
            item {
                TextButton(onClick = onChangeAvatarFromGallery) {
                    Text("Cambiar foto de perfil")
                }
            }

            // ===== NOMBRE + USERNAME + CAMBIO DE USER =====
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = data.displayName,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = data.handle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(4.dp))

                    TextButton(onClick = { showEditDialog = true }) {
                        Text("Cambiar nombre de usuario")
                    }
                }
            }

            // ===== STATS =====
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatChip(value = data.gamesCount, label = "Juegos")
                    StatChip(value = data.friendsCount, label = "Amigos")
                    StatChip(value = data.achievementsCount, label = "Logros")
                }
            }

            // ===== TROFEOS =====
            item { SectionTitle("Trofeos") }
            item {
                if (isNewUser) {
                    Text(
                        "Todavía no tienes trofeos. ¡Empieza a jugar! 🎮",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    TrophyRow(t = data.trophies)
                }
            }

            // ===== JUGADO RECIENTEMENTE =====
            item { SectionTitle("Jugado recientemente") }
            item {
                if (data.recentGames.isEmpty()) {
                    Text(
                        "Aún no hay juegos recientes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    RecentlyPlayedFlow(games = data.recentGames)
                }
            }

            // ===== BOTÓN CERRAR SESIÓN =====
            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }

    // ===== DIÁLOGO CAMBIAR USERNAME =====
    if (showEditDialog) {
        var newName by remember { mutableStateOf(data.handle.removePrefix("@")) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Cambiar nombre de usuario") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nuevo usuario") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onChangeUsername(newName)
                            showEditDialog = false
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// ====== Componentes ======
@Composable
private fun StatChip(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun TrophyRow(t: TrophyCounts) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        TrophyItem("🏆", "Platino", t.platinum)
        TrophyItem("🥇", "Oro", t.gold)
        TrophyItem("🥈", "Plata", t.silver)
        TrophyItem("🥉", "Bronce", t.bronze)
    }
}

@Composable
private fun TrophyItem(emoji: String, label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentlyPlayedFlow(games: List<RecentGame>) {
    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        games.forEach { g ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = painterResource(id = g.imageRes),
                    contentDescription = g.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
