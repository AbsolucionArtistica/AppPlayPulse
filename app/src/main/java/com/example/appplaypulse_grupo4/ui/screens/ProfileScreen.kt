package com.example.appplaypulse_grupo4.ui.screens

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
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onClose: () -> Unit) {
    val scroll = rememberScrollState()

    // Datos simulados
    val username = "Ferna_nda_k"
    val profileImage = R.drawable.elena
    val games = listOf("Apex Legends", "Magic Arena", "Final Fantasy XIV")
    val friends = listOf("Nuggw", "Raygimon21")
    val achievements = listOf("🏆 1000 horas jugadas", "🎯 10 victorias seguidas", "💬 50 amigos agregados")

    Scaffold(
        topBar = { TopNavBar(title = "Perfil") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil
            Image(
                painter = painterResource(id = profileImage),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre de usuario
            Text(
                text = username,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Juegos
            SectionTitle("🎮 Juegos favoritos")
            games.forEach { game ->
                Text("• $game", style = MaterialTheme.typography.bodyLarge)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Amigos
            SectionTitle("🤝 Amigos")
            friends.forEach { friend ->
                Text("• $friend", style = MaterialTheme.typography.bodyLarge)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Logros
            SectionTitle("🏆 Logros")
            achievements.forEach { logro ->
                Text(logro, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón cerrar
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Volver", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}
