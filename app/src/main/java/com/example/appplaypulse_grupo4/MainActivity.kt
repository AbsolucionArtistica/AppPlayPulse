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
import com.example.appplaypulse_grupo4.api.backend.BackendClient
import com.example.appplaypulse_grupo4.api.backend.RemoteBackendDataSource
import com.example.appplaypulse_grupo4.database.AppDatabase
import com.example.appplaypulse_grupo4.database.dto.FeedItem
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
import com.example.appplaypulse_grupo4.ui.screens.GameManager
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
import com.example.appplaypulse_grupo4.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import android.util.Log
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppPlayPulse_Grupo4Theme {
                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()
                val backendDataSource = remember { RemoteBackendDataSource(BackendClient.create()) }

                // BD Room
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

                // Semillas basicas para sugerencias de amigos
                LaunchedEffect(db) {
                    withContext(Dispatchers.IO) {
                        if (db.userDao().getUserCount() == 0) {
                            val seedUsers = listOf(
                                User(
                                    nombre = "Ana",
                                    apellido = "Lopez",
                                    edad = 22,
                                    email = "ana@example.com",
                                    phone = "+56 9 11111111",
                                    username = "AnaL",
                                    password = "PassWord1"
                                ),
                                User(
                                    nombre = "Bruno",
                                    apellido = "Diaz",
                                    edad = 28,
                                    email = "bruno@example.com",
                                    phone = "+56 9 22222222",
                                    username = "BrunoD",
                                    password = "ClaveFuerte2"
                                ),
                                User(
                                    nombre = "Carla",
                                    apellido = "Reyes",
                                    edad = 19,
                                    email = "carla@example.com",
                                    phone = "+56 9 33333333",
                                    username = "CarlaR",
                                    password = "Segura123A"
                                ),
                                User(
                                    nombre = "Diego",
                                    apellido = "Soto",
                                    edad = 24,
                                    email = "diego@example.com",
                                    phone = "+56 9 44444444",
                                    username = "DiegoS",
                                    password = "Contra456B"
                                )
                            )
                            seedUsers.forEach { user ->
                                db.userDao().insertUser(user)
                            }
                        }
                    }
                }

                // Sesion
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }
                var currentUserName by rememberSaveable { mutableStateOf<String?>(null) }
                var currentUserId by rememberSaveable { mutableStateOf<Long?>(null) }
                var remoteUserId by rememberSaveable { mutableStateOf<String?>(null) }

                // Datos de perfil para UI
                var profileUsername by rememberSaveable { mutableStateOf<String?>(null) }
                var profileAvatarResName by rememberSaveable { mutableStateOf<String?>(null) }
                var profileAvatarUri by rememberSaveable { mutableStateOf<String?>(null) }

                // Juegos recientes (perfil)
                var recentGames by remember { mutableStateOf<List<RecentGame>>(emptyList()) }

                // Foto desde galeria
                val galleryLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
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

                // FEED de comunidad desde la BD
                val feed by postRepo.getFeed().collectAsState(initial = emptyList())
                var backendFeed by remember { mutableStateOf<List<FeedItem>>(emptyList()) }
                var isSyncingFeed by remember { mutableStateOf(false) }

                val syncBackendUser: suspend (User, String) -> Unit = { user, plainPassword ->
                    val remote = backendDataSource.ensureUser(user, plainPassword)
                    remote
                        .onSuccess { remoteUserId = it.id }
                        .onFailure { err ->
                            Log.e("MainActivity", "No se pudo sincronizar usuario remoto", err)
                            Toast.makeText(
                                ctx,
                                "No se pudo sincronizar con el servidor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

                // Cada vez que cambia el usuario recargar amigos, juegos recientes y sugerencias
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

                LaunchedEffect(isAuthenticated) {
                    if (isAuthenticated) {
                        isSyncingFeed = true
                        val result = backendDataSource.fetchFeed()
                        result
                            .onSuccess { backendFeed = it }
                            .onFailure {
                                Toast.makeText(
                                    ctx,
                                    "No se pudo sincronizar el feed remoto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        isSyncingFeed = false
                    } else {
                        backendFeed = emptyList()
                        remoteUserId = null
                    }
                }

                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (!isAuthenticated) {
                            // LOGIN / REGISTRO
                            AuthScreen(
                            onLogin = { field, password, onResult ->
                                scope.launch {
                                    val result = userRepo.login(field, password)
                                    result
                                        .onSuccess { user ->
                                            isAuthenticated = true
                                            currentUserName = user.username
                                            currentUserId = user.id
                                            profileUsername = user.username
                                            profileAvatarResName = null
                                            profileAvatarUri = null
                                            syncBackendUser(user, password)
                                            backendDataSource.fetchFeed()
                                                .onSuccess { backendFeed = it }
                                            onResult(true, "Bienvenid@ ${user.username}")
                                        }
                                        .onFailure { ex ->
                                            onResult(
                                                false,
                                                ex.message ?: "Usuario o contrasena incorrectos"
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
                                        result
                                            .onSuccess { user ->
                                                isAuthenticated = true
                                                currentUserName = user.username
                                                currentUserId = user.id
                                            profileUsername = user.username
                                            profileAvatarResName = null
                                            profileAvatarUri = null
                                            syncBackendUser(user, password)
                                            backendDataSource.fetchFeed()
                                                .onSuccess { backendFeed = it }
                                            onResult(true, "Bienvenid@ ${user.username}")
                                        }
                                        .onFailure { ex ->
                                            onResult(
                                                false,
                                                    ex.message ?: "Error al registrar usuario"
                                                )
                                            }
                                    }
                                },
                                onRegisterWithGoogle = { name, email, onResult ->
                                    scope.launch {
                                        val fullName = name.trim().ifBlank { "Jugador Google" }
                                        val first = fullName.split(" ").firstOrNull().orEmpty()
                                        val last = fullName.split(" ").drop(1).joinToString(" ").ifBlank { "Google" }
                                        val usernameSeed = email.substringBefore("@")
                                            .replace(Regex("[^A-Za-z0-9_]"), "_")
                                            .take(15)
                                        val uniqueSuffix = Random.nextInt(1000, 9999)
                                        val usernameFinal = "$usernameSeed$uniqueSuffix"
                                        val phone = "+56 9 " + Random.nextInt(10_000_000, 99_999_999).toString()

                                        val result = userRepo.registerUser(
                                            nombre = first.ifBlank { "Jugador" },
                                            apellido = last,
                                            edad = 18,
                                            email = email,
                                            phone = phone,
                                            username = usernameFinal,
                                            password = "GoogleTmp!1"
                                        )
                                        result
                                            .onSuccess { user ->
                                                isAuthenticated = true
                                                currentUserName = user.username
                                                currentUserId = user.id
                                            profileUsername = user.username
                                            profileAvatarResName = null
                                            profileAvatarUri = null
                                            syncBackendUser(user, "GoogleTmp!1")
                                            backendDataSource.fetchFeed()
                                                .onSuccess { backendFeed = it }
                                            onResult(true, "Cuenta creada con Google: ${user.username}")
                                        }
                                        .onFailure { ex ->
                                            onResult(
                                                false,
                                                    ex.message ?: "No se pudo crear la cuenta con Google"
                                                )
                                            }
                                    }
                                }
                            )
                        } else {
                            // APP AUTENTICADA

                            // Home por defecto
                            if (!showFriends && !showGames && !showCommunity && !showProfile) {
                                HomeScreen(
                                    username = profileUsername ?: currentUserName,
                                    friends = homeFriends
                                )
                            }

                            // Juegos (vinculado a BD por usuario)
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

                            // Amigos (usa la BD por usuario)
                            if (showFriends) {
                                FriendsMockupScreen(
                                    currentUsername = profileUsername ?: currentUserName,
                                    friends = homeFriends,
                                    suggestedUsers = suggestedUsers,
                                    onClose = {
                                        showFriends = false
                                    },
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

                            // Comunidad (tipo X/Twitter)
                            if (showCommunity) {
                                SocialFeedScreen(
                                    currentUsername = profileUsername ?: currentUserName ?: "",
                                    hasFriends = homeFriends.isNotEmpty(),
                                    posts = if (backendFeed.isNotEmpty()) backendFeed else feed,
                                    onPublishPost = { text, location, link, imageUri ->
                                        val uid = currentUserId
                                        if (uid != null && text.isNotBlank()) {
                                            scope.launch {
                                                val remoteId = remoteUserId
                                                if (remoteId != null) {
                                                    backendDataSource.publishPost(
                                                        remoteUserId = remoteId,
                                                        username = profileUsername
                                                            ?: currentUserName ?: "",
                                                        content = text,
                                                        location = location,
                                                        link = link,
                                                        imageUri = imageUri
                                                    )
                                                        .onSuccess { item ->
                                                            backendFeed = listOf(item) + backendFeed
                                                        }
                                                        .onFailure {
                                                            Toast.makeText(
                                                                ctx,
                                                                "No se pudo publicar en el servidor",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                } else {
                                                    Toast.makeText(
                                                        ctx,
                                                        "Sin usuario remoto, vuelve a iniciar sesion",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                postRepo.addPost(
                                                    userId = uid,
                                                    content = text,
                                                    location = location,
                                                    link = link,
                                                    imageUri = imageUri
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

                            // Perfil
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
                                        remoteUserId = null
                                        profileUsername = null
                                        profileAvatarResName = null
                                        profileAvatarUri = null
                                        homeFriends = emptyList()
                                        recentGames = emptyList()
                                        suggestedUsers = emptyList()
                                        backendFeed = emptyList()
                                        isSyncingFeed = false
                                        showProfile = false
                                        showFriends = false
                                        showGames = false
                                        showCommunity = false
                                        Toast.makeText(ctx, "Sesion cerrada", Toast.LENGTH_SHORT)
                                            .show()
                                    },
                                    data = profileData,
                                    onChangeUsername = { newUser ->
                                        val uid = currentUserId
                                        if (uid != null) {
                                            scope.launch {
                                                val result = userRepo.updateUsername(uid, newUser)
                                                result
                                                    .onSuccess { updated ->
                                                        profileUsername = updated.username
                                                        currentUserName = updated.username
                                                        Toast.makeText(
                                                            ctx,
                                                            "Nombre de usuario actualizado",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    .onFailure { ex ->
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

                            // Menu lateral
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
 * SEMILLAS Y HELPERS
 * ================================== */

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

        val (gameTitle, gameResName, hoursText) = when (fe.name) {
            "Nuggw" -> Triple("FINAL FANTASY XIV", "finalfantasy", "100 horas jugadas")
            "Raygimon21" -> Triple("Magic Arena", "arena", "160 horas jugadas")
            "Ferna_nda_k" -> Triple("Apex Legends", "apex", "3500 horas jugadas")
            "Eth3rn4l" -> Triple("New World Aeternum", "nw", "3200 horas jugadas")
            else -> Triple("Jugando ahora", "lol", "-")
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
            hours = hoursText,
            isOnline = fe.isOnline
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
 * Devuelve el nombre del recurso drawable asociado a un usuario.
 * Se guarda en la BD (FriendEntity.avatarResName).
 */
fun avatarResNameForUser(username: String): String =
    when (username) {
        "Nuggw" -> "giphy"
        "Raygimon21" -> "agua"
        "Ferna_nda_k" -> "elena"
        "Eth3rn4l" -> "nw"
        else -> "elena"
    }
