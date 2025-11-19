package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import com.example.appplaypulse_grupo4.R
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar

// ==== Modelo de post de comunidad ====
data class CommunityPost(
    val id: Long,
    val username: String,
    val avatarRes: Int,
    val content: String,
    val location: String? = null,
    val link: String? = null,
    val hasFile: Boolean = false,
    val imageRes: Int? = null,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val comments: Int = 0,
    val replies: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToGames: () -> Unit = {},
    onNavigateToFriends: () -> Unit = {}
) {
    // ✅ Posts con algunos amigos precargados
    val posts = remember {
        mutableStateListOf(
            CommunityPost(
                id = 1L,
                username = "Nuggw",
                avatarRes = R.drawable.giphy,
                content = "Por fin terminé raid en Final Fantasy XIV, 100% worth 😮‍💨",
                location = "Limsa Lominsa (?)",
                likes = 5,
                comments = 2
            ),
            CommunityPost(
                id = 2L,
                username = "Raygimon21",
                avatarRes = R.drawable.agua,
                content = "¿Alguien se apunta a unas partidas de Magic Arena esta noche?",
                link = "https://magic.wizards.com",
                likes = 3,
                comments = 1
            ),
            CommunityPost(
                id = 3L,
                username = "Ferna_nda_k",
                avatarRes = R.drawable.elena,
                content = "Otro día tryhardeando Apex Legends y aún no sale el 20 bomb. 😭",
                imageRes = R.drawable.apex,
                likes = 10,
                comments = 4
            )
        )
    }

    // Campos del composer (para escribir un nuevo post)
    var text by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var attachFile by remember { mutableStateOf(false) }
    var attachImage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopNavBar(title = "Comunidad") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ==== Lista de posts ====
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(posts.reversed(), key = { it.id }) { post ->
                    CommunityPostCard(
                        post = post,
                        onLikeClick = { clicked ->
                            val index = posts.indexOfFirst { it.id == clicked.id }
                            if (index != -1) {
                                val current = posts[index]
                                val newLiked = !current.likedByMe
                                val newLikes = if (newLiked) current.likes + 1
                                else (current.likes - 1).coerceAtLeast(0)
                                posts[index] = current.copy(
                                    likedByMe = newLiked,
                                    likes = newLikes
                                )
                            }
                        },
                        onReplySubmit = { clicked, replyText ->
                            if (replyText.isNotBlank()) {
                                val index = posts.indexOfFirst { it.id == clicked.id }
                                if (index != -1) {
                                    val current = posts[index]
                                    posts[index] = current.copy(
                                        replies = current.replies + replyText,
                                        comments = current.comments + 1
                                    )
                                }
                            }
                        }
                    )
                }
            }

            Divider()

            // ==== Composer tipo X/Twitter ====
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar propio
                    Image(
                        painter = painterResource(id = R.drawable.elena),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = { Text("¿Qué está pasando?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp),
                            singleLine = false,
                            maxLines = 4
                        )

                        // Línea con resumen de adjuntos
                        if (location.isNotEmpty() || link.isNotEmpty() || attachFile || attachImage) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = buildString {
                                    if (location.isNotEmpty()) append("📍 $location   ")
                                    if (link.isNotEmpty()) append("🔗 Link agregado   ")
                                    if (attachFile) append("📎 Archivo adjunto   ")
                                    if (attachImage) append("🖼 Foto adjunta")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconChip(text = "📍", active = location.isNotEmpty()) {
                            location = if (location.isEmpty()) "Pudahuel, Santiago" else ""
                        }
                        IconChip(text = "🔗", active = link.isNotEmpty()) {
                            link = if (link.isEmpty()) "https://www.example.com" else ""
                        }
                        IconChip(text = "📎", active = attachFile) {
                            attachFile = !attachFile
                        }
                        IconChip(text = "🖼", active = attachImage) {
                            attachImage = !attachImage
                        }
                    }

                    val canPost = text.isNotBlank() ||
                            location.isNotEmpty() ||
                            link.isNotEmpty() ||
                            attachFile || attachImage

                    Button(
                        onClick = {
                            posts.add(
                                CommunityPost(
                                    id = System.currentTimeMillis(),
                                    username = "Ferna_nda_k",
                                    avatarRes = R.drawable.elena,
                                    content = text,
                                    location = location.ifBlank { null },
                                    link = link.ifBlank { null },
                                    hasFile = attachFile,
                                    imageRes = if (attachImage) R.drawable.apex else null
                                )
                            )
                            text = ""
                            location = ""
                            link = ""
                            attachFile = false
                            attachImage = false
                        },
                        enabled = canPost
                    ) {
                        Text("Postear")
                    }
                }
            }
        }
    }
}

// ==== Chip de icono simple (emoji) ====
@Composable
private fun IconChip(text: String, active: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (active)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else
            MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 18.sp
        )
    }
}

// ==== Tarjeta visual del post + like + responder ====
@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onLikeClick: (CommunityPost) -> Unit,
    onReplySubmit: (CommunityPost, String) -> Unit
) {
    var showReplyBox by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.avatarRes),
                    contentDescription = post.username,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (post.location != null) {
                        Text(
                            text = "📍 ${post.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Texto
            if (post.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp
                )
            }

            // Link
            if (post.link != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.link,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Archivo
            if (post.hasFile) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📎 Archivo adjunto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Imagen
            post.imageRes?.let { img ->
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = img),
                    contentDescription = "Imagen adjunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de acciones: Like + Responder
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onLikeClick(post) }) {
                    Text(
                        text = if (post.likedByMe) "❤ ${post.likes}" else "♡ ${post.likes}",
                        fontSize = 13.sp
                    )
                }

                TextButton(onClick = { showReplyBox = !showReplyBox }) {
                    Text(
                        text = "💬 Responder (${post.comments})",
                        fontSize = 13.sp
                    )
                }
            }

            // Caja para responder
            if (showReplyBox) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Escribe una respuesta...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    maxLines = 3
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onReplySubmit(post, replyText)
                            replyText = ""
                            showReplyBox = false
                        },
                        enabled = replyText.isNotBlank()
                    ) {
                        Text("Responder")
                    }
                }
            }

            // Lista de respuestas
            if (post.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    post.replies.forEach { reply ->
                        Text(
                            text = "↪ $reply",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
