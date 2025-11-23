package com.example.appplaypulse_grupo4

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.appplaypulse_grupo4.database.AppDatabase
import com.example.appplaypulse_grupo4.database.entity.FriendEntity
import com.example.appplaypulse_grupo4.database.entity.GameEntity
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.entity.UserGameEntity
import com.example.appplaypulse_grupo4.database.repository.PostRepository
import com.example.appplaypulse_grupo4.database.repository.UserRepository
import com.example.appplaypulse_grupo4.ui.components.AnimatedSideMenu
import com.example.appplaypulse_grupo4.ui.screens.*
import com.example.appplaypulse_grupo4.ui.theme.AppPlayPulse_Grupo4Theme
import com.example.appplaypulse_grupo4.ui.theme.Friend
import com.example.appplaypulse_grupo4.ui.theme.GameManager
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
import com.example.appplaypulse_grupo4.R
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppPlayPulse_Grupo4Theme {
                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()

                // 🗄️ BD Room
                val db = remember {
                    Room.databaseBuilder(
                        ctx,
                        AppDatabase::class.java,
                        "playpulse.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }

                val userRepo = remember { UserRepository(db.userDao()) }
                val postRepo = remember { PostRepository(db.postDao()) }

                // 🔐 Sesión
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }
                var currentUserName by rememberSaveable { mutableStateOf<String?>(null) }
                var currentUserId by rememberSaveable { mutableStateOf<Long?>(null) }

                // 👤 Datos de perfil para UI
                var profileUsername by rememberSaveable { mutableStateOf<String?>(null) }
                var profileAvatarResName by rememberSaveable { mutableStateOf<String?>(null) }
                var profileAvatarUri by rememberSaveable { mutableStateOf<String?>(null) }

                // 🎮 Juegos recientes (perfil)
                var recentGames by remember { mutableStateOf<List<RecentGame>>(emptyList()) }

                // 📸 Foto desde galería
                val galleryLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                        if (uri != null) {
                            profileAvatarUri = uri.toString()
                            Toast.makeText(ctx, "Foto actualizada", Toast.LENGTH_SHORT).show()
                        }
                    }

                // Flags de pantallas
                var showFriends by remember { mutableStateOf(false) }
                var showGames by remember { mutableStateOf(false) }
                var showCommunity by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }

                // Amigos que se muestran en el Home (por usuario)
                var homeFriends by remember { mutableStateOf<List<Friend>>(emptyList()) }

                // Usuarios sugeridos para agregar como amigos
                var suggestedUsers by remember { mutableStateOf<List<User>>(emptyList()) }

                // 🌱 Sembrar juegos base (solo una vez)
                LaunchedEffect(Unit) {
                    seedGamesIfNeeded(db)
                }

                // 📡 FEED de comunidad desde la BD
                val feed by postRepo.getFeed().collectAsState(initial = emptyList())

                // Cada vez que cambia el usuario → recargar amigos, juegos recientes y sugerencias
                LaunchedEffect(currentUserId) {
                    val uid = currentUserId
                    if (uid == null) {
                        homeFriends = emptyList()
                        recentGames = emptyList()
                        suggestedUsers = emptyList()
                    } else {
                        homeFriends = loadFriendsForHome(uid, db, ctx)
                        recentGames = loadRecentGamesForProfile(uid, db, ctx)
                        suggestedUsers = db.userDao().getSuggestedFriends(uid)
                    }
                }

                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (!isAuthenticated) {
                            // =========================
                            // 🔐 LOGIN / REGISTRO
                            // =========================
                            AuthScreen(
                                onLogin = { field, password, onResult ->
                                    scope.launch {
                                        val result = userRepo.login(field, password)
                                        result.onSuccess { user ->
                                            isAuthenticated = true
                                            currentUserName = user.username
                                            currentUserId = user.id
                                            profileUsername = user.username
                                            profileAvatarResName = null
                                            profileAvatarUri = null
                                            onResult(true, "Bienvenid@ ${user.username}")
                                        }.onFailure { ex ->
                                            onResult(
                                                false,
                                                ex.message ?: "Usuario o contraseña incorrectos"
                                            )
                                        }
                                    }
                                },
                                onRegister = { nombre, apellido, edadStr, correo, telefono, username, password, onResult ->
                                    scope.launch {
                                        val edad = edadStr.toIntOrNull() ?: 0
                                        val result = userRepo.registerUser(
                                            nombre = nombre,
                                            apellido = apellido,
                                            edad = edad,
                                            email = correo,
                                            phone = telefono,
                                            username = username,
                                            password = password
                                        )
                                        result.onSuccess { user ->
                                            isAuthenticated = true
                                            currentUserName = user.username
                                            currentUserId = user.id
                                            profileUsername = user.username
                                            profileAvatarResName = null
                                            profileAvatarUri = null
                                            onResult(true, "Bienvenid@ ${user.username}")
                                        }.onFailure { ex ->
                                            onResult(
                                                false,
                                                ex.message ?: "Error al registrar usuario"
                                            )
                                        }
                                    }
                                }
                            )
                        } else {
                            // =========================
                            // ✅ APP AUTENTICADA
                            // =========================

                            // 🏠 Home por defecto
                            if (!showFriends && !showGames && !showCommunity && !showProfile) {
                                HomeScreen(
                                    username = profileUsername ?: currentUserName,
                                    friends = homeFriends
                                )
                            }

                            // 🎮 Juegos (vinculado a BD por usuario)
                            if (showGames) {
                                GameManager(
                                    db = db,
                                    currentUserId = currentUserId,
                                    onGameAdded = {
                                        val uid = currentUserId
                                        if (uid != null) {
                                            scope.launch {
                                                recentGames = loadRecentGamesForProfile(uid, db, ctx)
                                            }
                                        }
                                    }
                                )
                            }

                            // 👥 Amigos (usa la BD por usuario)
                            if (showFriends) {
                                FriendsMockupScreen(
                                    currentUsername = profileUsername ?: currentUserName,
                                    friends = homeFriends,
                                    suggestedUsers = suggestedUsers,
                                    onClose = { showFriends = false },
                                    onAddFriendToDb = { friendUser ->
                                        val uid = currentUserId
                                        if (uid != null) {
                                            scope.launch {
                                                db.friendDao().insertFriend(
                                                    FriendEntity(
                                                        ownerUserId = uid,
                                                        name = friendUser.username,
                                                        avatarResName = avatarResNameForUser(friendUser.username),
                                                        isOnline = true
                                                    )
                                                )
                                                homeFriends = loadFriendsForHome(uid, db, ctx)
                                                suggestedUsers = db.userDao().getSuggestedFriends(uid)
                                            }
                                        }
                                    }
                                )
                            }

                            // 💬 Comunidad (tipo X/Twitter)
                            if (showCommunity) {
                                SocialFeedScreen(
                                    currentUsername = profileUsername ?: currentUserName ?: "",
                                    hasFriends = homeFriends.isNotEmpty(),
                                    posts = feed,
                                    onPublishPost = { text, location, link ->
                                        val uid = currentUserId
                                        if (uid != null && text.isNotBlank()) {
                                            scope.launch {
                                                postRepo.addPost(
                                                    userId = uid,
                                                    content = text,
                                                    location = location,
                                                    link = link
                                                )
                                            }
                                        }
                                    },
                                    onNavigateToHome = {
                                        showCommunity = false
                                        showFriends = false
                                        showGames = false
                                        showProfile = false
                                    },
                                    onNavigateToGames = {
                                        showGames = true
                                        showCommunity = false
                                        showFriends = false
                                        showProfile = false
                                    },
                                    onNavigateToFriends = {
                                        showFriends = true
                                        showCommunity = false
                                        showGames = false
                                        showProfile = false
                                    }
                                )
                            }

                            // 🧑 Perfil
                            if (showProfile) {
                                val avatarResId: Int? =
                                    profileAvatarResName?.let { resName ->
                                        val id = ctx.resources.getIdentifier(
                                            resName,
                                            "drawable",
                                            ctx.packageName
                                        )
                                        if (id != 0) id else null
                                    }

                                val profileData = ProfileData(
                                    displayName = profileUsername ?: currentUserName
                                    ?: "Nuevo jugador",
                                    handle = "@${profileUsername ?: currentUserName ?: "usuario"}",
                                    avatarRes = avatarResId,
                                    avatarUri = profileAvatarUri,
                                    trophies = TrophyCounts(0, 0, 0, 0),
                                    recentGames = recentGames,
                                    friendsCount = homeFriends.size,
                                    gamesCount = recentGames.size,
                                    achievementsCount = 0
                                )

                                ProfileScreen(
                                    onClose = { showProfile = false },
                                    onLogout = {
                                        isAuthenticated = false
                                        currentUserId = null
                                        currentUserName = null
                                        profileUsername = null
                                        profileAvatarResName = null
                                        profileAvatarUri = null
                                        homeFriends = emptyList()
                                        recentGames = emptyList()
                                        suggestedUsers = emptyList()
                                        showProfile = false
                                        showFriends = false
                                        showGames = false
                                        showCommunity = false
                                        Toast.makeText(ctx, "Sesión cerrada", Toast.LENGTH_SHORT)
                                            .show()
                                    },
                                    data = profileData,
                                    onChangeUsername = { newUser ->
                                        val uid = currentUserId
                                        if (uid != null) {
                                            scope.launch {
                                                val result = userRepo.updateUsername(uid, newUser)
                                                result.onSuccess { updated ->
                                                    profileUsername = updated.username
                                                    currentUserName = updated.username
                                                    Toast.makeText(
                                                        ctx,
                                                        "Nombre de usuario actualizado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }.onFailure { ex ->
                                                    Toast.makeText(
                                                        ctx,
                                                        ex.message
                                                            ?: "Error al actualizar nombre",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    },
                                    onChangeAvatarFromGallery = {
                                        galleryLauncher.launch("image/*")
                                    }
                                )
                            }

                            // 🎛️ Menú lateral
                            AnimatedSideMenu(
                                onHomeClick = {
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    showProfile = false
                                    Toast.makeText(
                                        ctx,
                                        "Volviendo al inicio",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onGamesClick = {
                                    showGames = true
                                    showFriends = false
                                    showCommunity = false
                                    showProfile = false
                                    Toast.makeText(
                                        ctx,
                                        "Abriendo Juegos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onFriendsClick = {
                                    showFriends = true
                                    showGames = false
                                    showCommunity = false
                                    showProfile = false
                                    Toast.makeText(
                                        ctx,
                                        "Abriendo Amigos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onCommunityClick = {
                                    showCommunity = true
                                    showFriends = false
                                    showGames = false
                                    showProfile = false
                                    Toast.makeText(
                                        ctx,
                                        "Abriendo Comunidad",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onProfileClick = {
                                    showProfile = true
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    Toast.makeText(
                                        ctx,
                                        "Abriendo Perfil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ==================================
 * 🌱 SEMILLAS Y HELPERS
 * ================================== */

suspend fun seedGamesIfNeeded(db: AppDatabase) {
    val gameDao = db.gameDao()
    val existing = gameDao.getAllGames()
    if (existing.isNotEmpty()) return

    val games = listOf(
        GameEntity(title = "Apex Legends", imageResName = "apex"),
        GameEntity(title = "Magic Arena", imageResName = "arena"),
        GameEntity(title = "Final Fantasy XIV", imageResName = "finalfantasy"),
        GameEntity(title = "League of Legends", imageResName = "lol"),
        GameEntity(title = "Minecraft", imageResName = "minecraft"),
        GameEntity(title = "New World Aeternum", imageResName = "nw")
    )
    gameDao.insertGames(games)
}

suspend fun loadFriendsForHome(
    userId: Long,
    db: AppDatabase,
    ctx: Context
): List<Friend> {
    val friendDao = db.friendDao()
    val friendEntities: List<FriendEntity> = friendDao.getFriendsForUser(userId)

    return friendEntities.map { fe ->
        val avatarRes = ctx.resources.getIdentifier(
            fe.avatarResName,
            "drawable",
            ctx.packageName
        ).takeIf { it != 0 } ?: R.drawable.elena

        // Juego que se muestra por amigo (puedes cambiar esto si quieres)
        val (gameTitle, gameResName, hoursText) = when (fe.name) {
            "Nuggw" -> Triple("FINAL FANTASY XIV", "finalfantasy", "100 horas jugadas")
            "Raygimon21" -> Triple("Magic Arena", "arena", "160 horas jugadas")
            "Ferna_nda_k" -> Triple("Apex Legends", "apex", "3500 horas jugadas")
            "Eth3rn4l" -> Triple("New World Aeternum", "nw", "3200 horas jugadas")
            else -> Triple("Jugando ahora", "lol", "—")
        }

        val gameImageRes = ctx.resources.getIdentifier(
            gameResName,
            "drawable",
            ctx.packageName
        ).takeIf { it != 0 } ?: R.drawable.apex

        Friend(
            name = fe.name,
            profileRes = avatarRes,
            gameName = gameTitle,
            gameImageRes = gameImageRes,
            hours = hoursText
        )
    }
}

suspend fun loadRecentGamesForProfile(
    userId: Long,
    db: AppDatabase,
    ctx: Context
): List<RecentGame> {
    val userGameDao = db.userGameDao()
    val recent: List<UserGameEntity> = userGameDao.getRecentGamesForUser(userId)

    return recent.map { ug ->
        val imgRes = ctx.resources.getIdentifier(
            ug.imageResName,
            "drawable",
            ctx.packageName
        ).takeIf { it != 0 } ?: R.drawable.apex

        RecentGame(
            title = ug.gameTitle,
            imageRes = imgRes
        )
    }
}

/**
 * Devuelve el *nombre* del recurso drawable asociado a un usuario.
 * Se guarda en la BD (FriendEntity.avatarResName).
 */
fun avatarResNameForUser(username: String): String =
    when (username) {
        "Nuggw"       -> "giphy"
        "Raygimon21"  -> "agua"
        "Ferna_nda_k" -> "elena"
        "Eth3rn4l"    -> "nw"
        else          -> "elena"   // avatar genérico para usuarios nuevos
    }
