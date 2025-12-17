package com.example.appplaypulse_grupo4

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
import com.example.appplaypulse_grupo4.api.backend.ApiUser
import com.example.appplaypulse_grupo4.api.backend.BackendClient
import com.example.appplaypulse_grupo4.api.backend.RemoteBackendDataSource
import com.example.appplaypulse_grupo4.database.dto.FeedItem
import com.example.appplaypulse_grupo4.ui.components.AnimatedSideMenu
import com.example.appplaypulse_grupo4.ui.screens.*
import com.example.appplaypulse_grupo4.ui.theme.AppPlayPulse_Grupo4Theme
import com.example.appplaypulse_grupo4.ui.theme.Friend
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
import com.example.appplaypulse_grupo4.R
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppPlayPulse_Grupo4Theme {
                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()
                val backend = remember { RemoteBackendDataSource(BackendClient.create()) }

                // Estado de sesion
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }
                var currentUserName by rememberSaveable { mutableStateOf<String?>(null) }
                var remoteUserId by rememberSaveable { mutableStateOf<String?>(null) }
                var isAdmin by rememberSaveable { mutableStateOf(false) }

                // Perfil
                var profileUsername by rememberSaveable { mutableStateOf<String?>(null) }
                var profileAvatarResName by rememberSaveable { mutableStateOf<String?>(null) }
                var profileAvatarUri by rememberSaveable { mutableStateOf<String?>(null) }

                // Datos remotos
                var backendFeed by remember { mutableStateOf<List<FeedItem>>(emptyList()) }
                var homeFriends by remember { mutableStateOf<List<Friend>>(emptyList()) }
                var suggestedUsers by remember { mutableStateOf<List<com.example.appplaypulse_grupo4.database.entity.User>>(emptyList()) }
                var recentGames by remember { mutableStateOf<List<RecentGame>>(emptyList()) }
                var adminUsers by remember { mutableStateOf<List<ApiUser>>(emptyList()) }

                // Navegacion UI
                var showFriends by remember { mutableStateOf(false) }
                var showGames by remember { mutableStateOf(false) }
                var showCommunity by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }
                var showUserManagement by remember { mutableStateOf(false) }

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

                // Sincronizar feed al autenticarse
                LaunchedEffect(isAuthenticated) {
                    if (isAuthenticated) {
                        backend.fetchFeed()
                            .onSuccess { backendFeed = it }
                            .onFailure {
                                Toast.makeText(ctx, "No se pudo cargar el feed remoto", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        backendFeed = emptyList()
                    }
                }

                // Sincronizar amigos, juegos y sugerencias
                LaunchedEffect(remoteUserId) {
                    val rid = remoteUserId
                    if (rid == null) {
                        homeFriends = emptyList()
                        recentGames = emptyList()
                        suggestedUsers = emptyList()
                        adminUsers = emptyList()
                    } else {
                        backend.fetchFriends(rid)
                            .onSuccess { list ->
                                homeFriends = list.map { f ->
                                    f.copy(
                                        profileRes = resolveAvatar(ctx, f.avatarResName),
                                        gameImageRes = resolveGameImage(ctx, f.gameName),
                                        hours = f.hours.ifBlank { "" }
                                    )
                                }
                            }
                        backend.fetchGames(rid)
                            .onSuccess { games ->
                                recentGames = games.map { g ->
                                    g.copy(imageRes = resolveGameImage(ctx, g.title))
                                }
                            }
                        backend.fetchUsers()
                            .onSuccess { users ->
                                suggestedUsers = users.map { api ->
                                    com.example.appplaypulse_grupo4.database.entity.User(
                                        id = 0,
                                        nombre = api.nombre,
                                        apellido = api.apellido,
                                        edad = api.edad,
                                        email = api.email,
                                        phone = api.phone,
                                        username = api.username,
                                        password = "remote"
                                    )
                                }
                                if (isAdmin) {
                                    adminUsers = users
                                }
                            }
                    }
                }

                // Sincronizar lista completa de usuarios para admin
                LaunchedEffect(isAdmin) {
                    if (isAdmin) {
                        backend.fetchUsers()
                            .onSuccess { adminUsers = it }
                            .onFailure {
                                adminUsers = emptyList()
                            }
                    } else {
                        adminUsers = emptyList()
                    }
                }

                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (!isAuthenticated) {
                            AuthScreen(
                                onLogin = { field, password, onResult ->
                                    scope.launch {
                                            backend.login(field, password)
                                                .onSuccess { user ->
                                                    isAuthenticated = true
                                                    remoteUserId = user.id
                                                    currentUserName = user.username
                                                    isAdmin = user.username.equals("admin", ignoreCase = true)
                                                    profileUsername = user.username
                                                    profileAvatarResName = null
                                                    profileAvatarUri = null
                                                    onResult(true, "Bienvenid@ ${user.username}")
                                                }
                                            .onFailure { ex ->
                                                onResult(false, ex.message ?: "Usuario o contrasena incorrectos")
                                            }
                                    }
                                },
                                onRegister = { nombre, apellido, edadStr, correo, telefono, username, password, onResult ->
                                    scope.launch {
                                        val edad = edadStr.toIntOrNull() ?: 0
                                        val user = com.example.appplaypulse_grupo4.database.entity.User(
                                            nombre = nombre,
                                            apellido = apellido,
                                            edad = edad,
                                            email = correo,
                                            phone = telefono,
                                            username = username,
                                            password = password
                                        )
                                            backend.register(user, password)
                                                .onSuccess { apiUser ->
                                                    isAuthenticated = true
                                                    remoteUserId = apiUser.id
                                                    currentUserName = apiUser.username
                                                    isAdmin = apiUser.username.equals("admin", ignoreCase = true)
                                                    profileUsername = apiUser.username
                                                    profileAvatarResName = null
                                                    profileAvatarUri = null
                                                    onResult(true, "Bienvenid@ ${apiUser.username}")
                                                }
                                            .onFailure { ex ->
                                                onResult(false, ex.message ?: "Error al registrar usuario")
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

                                        val user = com.example.appplaypulse_grupo4.database.entity.User(
                                            nombre = first.ifBlank { "Jugador" },
                                            apellido = last,
                                            edad = 18,
                                            email = email,
                                            phone = phone,
                                            username = usernameFinal,
                                            password = "GoogleTmp!1"
                                        )

                                            backend.register(user, "GoogleTmp!1")
                                                .onSuccess { apiUser ->
                                                    isAuthenticated = true
                                                    remoteUserId = apiUser.id
                                                    currentUserName = apiUser.username
                                                    isAdmin = apiUser.username.equals("admin", ignoreCase = true)
                                                    profileUsername = apiUser.username
                                                    profileAvatarResName = null
                                                    profileAvatarUri = null
                                                    onResult(true, "Cuenta creada con Google: ${apiUser.username}")
                                                }
                                            .onFailure { ex ->
                                                onResult(false, ex.message ?: "No se pudo crear la cuenta con Google")
                                            }
                                    }
                                }
                            )
                        } else {
                            if (!showFriends && !showGames && !showCommunity && !showProfile && !showUserManagement) {
                                HomeScreen(
                                    username = profileUsername ?: currentUserName,
                                    friends = homeFriends
                                )
                            }

                            if (showGames) {
                                GameManager(
                                    backendDataSource = backend,
                                    remoteUserId = remoteUserId,
                                    onGameAdded = {
                                        val rid = remoteUserId
                                        if (rid != null) {
                                            scope.launch {
                                                backend.fetchGames(rid)
                                                    .onSuccess { games ->
                                                        recentGames = games.map { g ->
                                                            g.copy(imageRes = resolveGameImage(ctx, g.title))
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                )
                            }

                            if (showUserManagement && isAdmin) {
                                UserManagementScreen(
                                    users = adminUsers,
                                    onRefresh = {
                                        scope.launch {
                                            backend.fetchUsers()
                                                .onSuccess { adminUsers = it }
                                                .onFailure {
                                                    Toast.makeText(ctx, "No se pudo actualizar usuarios", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    },
                                    onDeleteUser = { user ->
                                        scope.launch {
                                            backend.deleteUser(user.id)
                                                .onSuccess {
                                                    adminUsers = adminUsers.filterNot { it.id == user.id }
                                                    Toast.makeText(ctx, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                                                }
                                                .onFailure {
                                                    Toast.makeText(ctx, it.message ?: "Error al eliminar", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    },
                                    onClose = { showUserManagement = false }
                                )
                            }

                            if (showFriends) {
                                FriendsMockupScreen(
                                    currentUsername = profileUsername ?: currentUserName,
                                    friends = homeFriends,
                                    suggestedUsers = suggestedUsers,
                                    onClose = { showFriends = false },
                                    onAddFriendToDb = { friendUser ->
                                        val rid = remoteUserId
                                        if (rid != null) {
                                            scope.launch {
                                                backend.addFriend(
                                                    ownerUserId = rid,
                                                    friendUserId = null,
                                                    friendName = friendUser.username,
                                                    avatarResName = null
                                                ).onSuccess {
                                                    backend.fetchFriends(rid)
                                                        .onSuccess { list ->
                                                            homeFriends = list.map { f ->
                                                                f.copy(
                                                                    profileRes = resolveAvatar(ctx, f.avatarResName),
                                                                    gameImageRes = resolveGameImage(ctx, f.gameName),
                                                                    hours = f.hours.ifBlank { "" }
                                                                )
                                                            }
                                                        }
                                                }.onFailure {
                                                    Toast.makeText(ctx, "No se pudo agregar amigo", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            if (showCommunity) {
                                SocialFeedScreen(
                                    currentUsername = profileUsername ?: currentUserName ?: "",
                                    hasFriends = homeFriends.isNotEmpty(),
                                    posts = backendFeed,
                                    onPublishPost = { text, location, link, imageUri ->
                                        val rid = remoteUserId
                                        if (rid != null && text.isNotBlank()) {
                                            scope.launch {
                                                backend.publishPost(
                                                    remoteUserId = rid,
                                                    username = profileUsername ?: currentUserName ?: "",
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
                                    displayName = profileUsername ?: currentUserName ?: "Nuevo jugador",
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
                                        currentUserName = null
                                        remoteUserId = null
                                        isAdmin = false
                                        profileUsername = null
                                        profileAvatarResName = null
                                        profileAvatarUri = null
                                        homeFriends = emptyList()
                                        recentGames = emptyList()
                                        suggestedUsers = emptyList()
                                        adminUsers = emptyList()
                                        backendFeed = emptyList()
                                        showProfile = false
                                        showFriends = false
                                        showGames = false
                                        showCommunity = false
                                        showUserManagement = false
                                        Toast.makeText(ctx, "Sesion cerrada", Toast.LENGTH_SHORT).show()
                                    },
                                    data = profileData,
                                    onChangeUsername = {
                                        Toast.makeText(ctx, "Cambiar username no esta soportado aun", Toast.LENGTH_SHORT).show()
                                    },
                                    onChangeAvatarFromGallery = {
                                        galleryLauncher.launch("image/*")
                                    }
                                )
                            }

                            AnimatedSideMenu(
                                onHomeClick = {
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    showProfile = false
                                    showUserManagement = false
                                    Toast.makeText(ctx, "Volviendo al inicio", Toast.LENGTH_SHORT).show()
                                },
                                onGamesClick = {
                                    showGames = true
                                    showFriends = false
                                    showCommunity = false
                                    showProfile = false
                                    showUserManagement = false
                                    Toast.makeText(ctx, "Abriendo Juegos", Toast.LENGTH_SHORT).show()
                                },
                                onFriendsClick = {
                                    showFriends = true
                                    showGames = false
                                    showCommunity = false
                                    showProfile = false
                                    showUserManagement = false
                                    Toast.makeText(ctx, "Abriendo Amigos", Toast.LENGTH_SHORT).show()
                                },
                                onCommunityClick = {
                                    showCommunity = true
                                    showFriends = false
                                    showGames = false
                                    showProfile = false
                                    showUserManagement = false
                                    Toast.makeText(ctx, "Abriendo Comunidad", Toast.LENGTH_SHORT).show()
                                },
                                onProfileClick = {
                                    showProfile = true
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    showUserManagement = false
                                    Toast.makeText(ctx, "Abriendo Perfil", Toast.LENGTH_SHORT).show()
                                },
                                onUserManagementClick = {
                                    if (isAdmin) {
                                        showUserManagement = true
                                        showFriends = false
                                        showGames = false
                                        showCommunity = false
                                        showProfile = false
                                        Toast.makeText(ctx, "Gestion de usuarios", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(ctx, "Solo admin", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                showUserManagement = isAdmin
                            )
                        }
                    }
                }
            }
        }
    }
}

fun resolveAvatar(ctx: android.content.Context, avatarResName: String?): Int {
    if (avatarResName.isNullOrBlank()) return R.drawable.elena
    return ctx.resources.getIdentifier(
        avatarResName,
        "drawable",
        ctx.packageName
    ).takeIf { it != 0 } ?: R.drawable.elena
}

fun resolveGameImage(ctx: android.content.Context, key: String?): Int {
    val normalized = key?.lowercase().orEmpty()
    val resName = when {
        "apex" in normalized -> "apex"
        "final" in normalized || "ffxiv" in normalized || "fantasy" in normalized -> "finalfantasy"
        "league" in normalized || "lol" in normalized -> "lol"
        "arena" in normalized || "magic" in normalized -> "arena"
        "minecraft" in normalized -> "minecraft"
        "new world" in normalized || "aeternum" in normalized -> "nw"
        else -> "apex"
    }
    return ctx.resources.getIdentifier(
        resName,
        "drawable",
        ctx.packageName
    ).takeIf { it != 0 } ?: R.drawable.apex
}
