package com.example.appplaypulse_grupo4.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appplaypulse_grupo4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String? = null,          // nombre del usuario logueado
    friends: List<Friend> = emptyList() // amigos desde la BD
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopNavBar(title = "PlayPulse") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 👋 Saludo
            Text(
                text = if (username != null) "👋 ¡Hola, $username!" else "👋 ¡Bienvenido!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "Tus amigos y sus juegos:",
                style = MaterialTheme.typography.titleMedium
            )

            if (friends.isEmpty()) {
                // 🆕 Cuenta sin amigos todavía
                Text(
                    text = "Aún no has agregado amigos. Cuando agregues amigos, aquí verás a qué están jugando.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                friends.forEach { friend ->
                    FriendCard(friend)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 🧩 Modelo de datos para la UI (NO es la entidad de Room)
data class Friend(
    val name: String,
    val profileRes: Int,   // ya no se usa para avatar, pero lo dejamos por compatibilidad
    val gameName: String,
    val gameImageRes: Int,
    val hours: String
)

/* Avatar con iniciales para Home */

@Composable
fun HomeInitialsAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val initials = name.trim()
        .split(" ", "_")
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

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

// 🎴 Tarjeta de amigo con juego
@Composable
fun FriendCard(friend: Friend) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar con iniciales
            HomeInitialsAvatar(
                name = friend.name,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${friend.gameName} • ${friend.hours}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Imagen del juego
        Image(
            painter = painterResource(id = friend.gameImageRes),
            contentDescription = friend.gameName,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        username = "Ferna_nda_k",
        friends = listOf(
            Friend(
                name = "Nuggw",
                profileRes = R.drawable.giphy,
                gameName = "FINAL FANTASY XIV",
                gameImageRes = R.drawable.finalfantasy,
                hours = "100 horas jugadas"
            ),
            Friend(
                name = "Raygimon21",
                profileRes = R.drawable.agua,
                gameName = "Magic Arena",
                gameImageRes = R.drawable.arena,
                hours = "160 horas jugadas"
            ),
            Friend(
                name = "Eth3rn4l",
                profileRes = R.drawable.nw,
                gameName = "New World Aeternum",
                gameImageRes = R.drawable.nw,
                hours = "3200 horas jugadas"
            )
        )
    )
}
