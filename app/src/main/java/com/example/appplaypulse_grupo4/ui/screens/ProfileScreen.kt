package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar

// ====== Modelo de datos ======
data class TrophyCounts(val platinum: Int, val gold: Int, val silver: Int, val bronze: Int)
data class RecentGame(val title: String, val imageRes: Int)
data class ProfileData(
    val displayName: String,
    val handle: String,
    val avatarRes: Int,
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
    data: ProfileData = ProfileData(
        displayName = "nohay tampocohay",
        handle = "Ferna_nda_k",
        avatarRes = R.drawable.elena,
        trophies = TrophyCounts(platinum = 0, gold = 39, silver = 82, bronze = 461),
        recentGames = listOf(
            RecentGame("Apex Legends", R.drawable.apex),
            RecentGame("Magic Arena", R.drawable.arena),
            RecentGame("Final Fantasy XIV", R.drawable.finalfantasy),
            RecentGame("League of Legends", R.drawable.lol)
        ),
        friendsCount = 50,
        gamesCount = 24,
        achievementsCount = 177 + 0 + 39 + 82 + 461
    )
) {
    Scaffold(topBar = { TopNavBar(title = "Perfil") }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(id = data.avatarRes),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(96.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
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
                }
            }
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
            item { SectionTitle("Trofeos") }
            item { TrophyRow(t = data.trophies) }
            item { SectionTitle("Jugado recientemente") }
            item { RecentlyPlayedFlow(games = data.recentGames) } // 👈 grilla sin scroll propio
        }
    }
}

// ====== Componentes ======
@Composable
private fun StatChip(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
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
        Text(text = count.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    .weight(1f)      // 2 por fila
                    .aspectRatio(1f) // cuadrado
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
