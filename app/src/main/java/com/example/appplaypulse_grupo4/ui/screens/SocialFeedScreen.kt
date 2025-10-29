package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appplaypulse_grupo4.R

// Modelo del post
data class Post(
    val author: String,
    val text: String,
    val imageRes: Int,
    val comments: MutableList<String> = mutableListOf(),
    var likes: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToGames: () -> Unit = {},
    onNavigateToFriends: () -> Unit = {}
) {
    val postList = remember { mutableStateListOf<Post>() }
    var newPostText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // 💙 Barra cyan idéntica a PlayPulse
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3DD4E7)) // mismo color cyan
                    .padding(vertical = 14.dp)
            ) {
                Text(
                    text = "Comunidad",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 📝 Campo para nueva publicación
            OutlinedTextField(
                value = newPostText,
                onValueChange = { newPostText = it },
                label = { Text("Publica algo para la comunidad...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (newPostText.isNotBlank()) {
                        postList.add(
                            0,
                            Post(
                                author = "Tú",
                                text = newPostText,
                                imageRes = R.drawable.elena
                            )
                        )
                        newPostText = ""
                    }
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3DD4E7)
                )
            ) {
                Text("Publicar", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📜 Lista de publicaciones
            if (postList.isEmpty()) {
                Text(
                    "No hay publicaciones todavía. ¡Sé la primera en escribir algo!",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(postList) { post ->
                        PostCard(post)
                    }
                }
            }
        }
    }
}

// 🎴 Tarjeta de publicación individual
@Composable
fun PostCard(post: Post) {
    var commentText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 👤 Autor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.imageRes),
                    contentDescription = post.author,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(post.author, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(post.text, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            // ❤️ Likes
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { post.likes++ }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color(0xFF3DD4E7)
                    )
                }
                Text("${post.likes}")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // 💬 Comentarios
            if (post.comments.isNotEmpty()) {
                post.comments.forEach { comment ->
                    Text("💬 $comment", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ✏️ Escribir comentario
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Comentar...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        post.comments.add(commentText)
                        commentText = ""
                    }
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3DD4E7)
                )
            ) {
                Text("Responder", color = Color.White)
            }
        }
    }
}
