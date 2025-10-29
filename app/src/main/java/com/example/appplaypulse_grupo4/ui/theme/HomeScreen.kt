package com.example.appplaypulse_grupo4.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appplaypulse_grupo4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // Lista de amigos simulada con imágenes de juegos
    val friends = listOf(
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
            name = "Ferna_nda_k",
            profileRes = R.drawable.elena,
            gameName = "Apex Legends",
            gameImageRes = R.drawable.apex,
            hours = "3500 horas jugadas"
        ),
        Friend(
                name = "Eth3rn4l",
        profileRes = R.drawable.Et3rn4l,
        gameName = "New World Aeternum",
        gameImageRes = R.drawable.nw,
        hours = "3200 horas jugadas"
    )
    )

    val scrollState = rememberScrollState() //  Control del scroll

    Scaffold(
        topBar = { TopNavBar(title = "PlayPulse") } //  Corrección aquí
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState) //  Permite desplazamiento
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "👋 ¡Bienvenido!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "Tus amigos y sus juegos:",
                style = MaterialTheme.typography.titleMedium
            )

            // Lista de amigos
            friends.forEach { friend ->
                FriendCard(friend)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

//  Modelo de datos
data class Friend(
    val name: String,
    val profileRes: Int,
    val gameName: String,
    val gameImageRes: Int,
    val hours: String
)

//  Tarjeta de amigo con juego
@Composable
fun FriendCard(friend: Friend) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Foto de perfil circular
            Image(
                painter = painterResource(id = friend.profileRes),
                contentDescription = friend.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
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
    HomeScreen()
}