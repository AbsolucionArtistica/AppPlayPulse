package com.example.appplaypulse_grupo4

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appplaypulse_grupo4.database.DatabaseHelper
import com.example.appplaypulse_grupo4.ui.components.AnimatedSideMenu
import com.example.appplaypulse_grupo4.ui.screens.AuthScreen
import com.example.appplaypulse_grupo4.ui.screens.FriendsMockupScreen
import com.example.appplaypulse_grupo4.ui.screens.ProfileScreen
import com.example.appplaypulse_grupo4.ui.screens.SocialFeedScreen
import com.example.appplaypulse_grupo4.ui.theme.AppPlayPulse_Grupo4Theme
import com.example.appplaypulse_grupo4.ui.theme.GameManagerScreen
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this)

        enableEdgeToEdge()

        setContent {
            AppPlayPulse_Grupo4Theme {
                val viewModel: MainViewModel = viewModel()
                val ctx = LocalContext.current
                val activity = this@MainActivity

                // Estado de autenticación
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }
                var showAuth by rememberSaveable { mutableStateOf(true) }
                var googleError by rememberSaveable { mutableStateOf<String?>(null) }

                // Estados de navegación interna (solo se usan cuando estás autenticado)
                var showFriends by remember { mutableStateOf(false) }
                var showGames by remember { mutableStateOf(false) }
                var showCommunity by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }

                val googleSignInClient = remember {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                    GoogleSignIn.getClient(activity, gso)
                }

                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        isAuthenticated = true
                        showAuth = false
                        showFriends = false; showGames = false; showCommunity = false
                        googleError = null
                        account?.let {
                            val displayName = it.displayName ?: it.email ?: "Google"
                            Toast.makeText(ctx, "Sesión iniciada como $displayName", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: ApiException) {
                        googleError = "No se pudo iniciar sesión con Google (${e.statusCode})"
                        Toast.makeText(ctx, googleError, Toast.LENGTH_SHORT).show()
                    }
                }

                val startGoogleSignIn: () -> Unit = {
                    val lastAccount = GoogleSignIn.getLastSignedInAccount(activity)
                    if (lastAccount != null) {
                        isAuthenticated = true
                        showAuth = false
                        showFriends = false; showGames = false; showCommunity = false
                        googleError = null
                        Toast.makeText(
                            ctx,
                            "Sesión iniciada como ${lastAccount.displayName ?: lastAccount.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                }

                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Si no está autenticado, siempre muestra AuthScreen
                        if (!isAuthenticated || showAuth) {
                            AuthScreen(
                                onClose = { /* opcional: puedes no permitir cerrar sin login */ },
                                onLoginSuccess = {
                                    isAuthenticated = true
                                    showAuth = false
                                    // opcional: reinicia vistas
                                    showFriends = false; showGames = false; showCommunity = false
                                },
                                onRegisterSuccess = {
                                    isAuthenticated = true
                                    showAuth = false
                                    showFriends = false; showGames = false; showCommunity = false
                                },
                                onGoogleAuth = startGoogleSignIn,
                                googleError = googleError
                            )
                        } else {
                            // Inicio
                            if (!showFriends && !showGames && !showCommunity) {
                                HomeScreen()
                            }

                            // Amigos
                            if (showFriends) {
                                FriendsMockupScreen(onClose = { showFriends = false })
                            }

                            // Juegos
                            if (showGames) {
                                GameManagerScreen()
                            }
                            if (showProfile) {
                                ProfileScreen(onClose = { showProfile = false })
                            }

                            // Comunidad
                            if (showCommunity) {
                                SocialFeedScreen(
                                    onNavigateToHome = {
                                        showCommunity = false
                                        showFriends = false
                                        showGames = false
                                    },
                                    onNavigateToGames = {
                                        showGames = true
                                        showCommunity = false
                                        showFriends = false
                                    },
                                    onNavigateToFriends = {
                                        showFriends = true
                                        showCommunity = false
                                        showGames = false
                                    }
                                )
                            }

                            // Menú lateral animado (solo si estás autenticado)
                            AnimatedSideMenu(
                                onHomeClick = {
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    Toast.makeText(ctx, "Volviendo al inicio", Toast.LENGTH_SHORT).show()
                                },
                                onGamesClick = {
                                    showGames = true
                                    showFriends = false
                                    showCommunity = false
                                    Toast.makeText(ctx, "Abriendo Juegos", Toast.LENGTH_SHORT).show()
                                },
                                onFriendsClick = {
                                    showFriends = true
                                    showGames = false
                                    showCommunity = false
                                    Toast.makeText(ctx, "Abriendo Amigos", Toast.LENGTH_SHORT).show()
                                },
                                onCommunityClick = {
                                    showCommunity = true
                                    showFriends = false
                                    showGames = false
                                    Toast.makeText(ctx, "Abriendo Comunidad", Toast.LENGTH_SHORT).show()

                                },
                                onProfileClick = {
                                    showProfile = true
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    Toast.makeText(ctx, "Abriendo Perfil", Toast.LENGTH_SHORT).show()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
