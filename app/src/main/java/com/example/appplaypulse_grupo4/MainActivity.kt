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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            AppPlayPulse_Grupo4Theme {
                val viewModel: MainViewModel = viewModel()
                val ctx = LocalContext.current

                // Estado de autenticacion
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }
                var showAuth by rememberSaveable { mutableStateOf(true) }

                // Estados de navegacion interna (solo se usan cuando ya estas autenticado)
                var showFriends by remember { mutableStateOf(false) }
                var showGames by remember { mutableStateOf(false) }
                var showCommunity by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }

                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        isAuthenticated = true
                        showAuth = false
                        showFriends = false; showGames = false; showCommunity = false; showProfile = false
                        val emailOrName = account?.email ?: account?.displayName ?: ""
                        val suffix = if (emailOrName.isNotBlank()) " ($emailOrName)" else ""
                        Toast.makeText(ctx, "Sesion con Google$suffix", Toast.LENGTH_SHORT).show()
                    } catch (e: ApiException) {
                        Toast.makeText(ctx, "Error al iniciar con Google (${e.statusCode})", Toast.LENGTH_SHORT).show()
                    }
                }

                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Si no esta autenticado, siempre muestra AuthScreen
                        if (!isAuthenticated || showAuth) {
                            AuthScreen(
                                onClose = { },
                                onLoginSuccess = {
                                    isAuthenticated = true
                                    showAuth = false
                                    showFriends = false; showGames = false; showCommunity = false; showProfile = false
                                },
                                onRegisterSuccess = {
                                    isAuthenticated = true
                                    showAuth = false
                                    showFriends = false; showGames = false; showCommunity = false; showProfile = false
                                },
                                onGoogleLogin = {
                                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                }
                            )
                        } else {
                            // Inicio
                            if (!showFriends && !showGames && !showCommunity && !showProfile) {
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

                            // Perfil
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

                            // Menu lateral animado (solo si estas autenticado)
                            AnimatedSideMenu(
                                onHomeClick = {
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    showProfile = false
                                    Toast.makeText(ctx, "Volviendo al inicio", Toast.LENGTH_SHORT).show()
                                },
                                onGamesClick = {
                                    showGames = true
                                    showFriends = false
                                    showCommunity = false
                                    showProfile = false
                                    Toast.makeText(ctx, "Abriendo Juegos", Toast.LENGTH_SHORT).show()
                                },
                                onFriendsClick = {
                                    showFriends = true
                                    showGames = false
                                    showCommunity = false
                                    showProfile = false
                                    Toast.makeText(ctx, "Abriendo Amigos", Toast.LENGTH_SHORT).show()
                                },
                                onCommunityClick = {
                                    showCommunity = true
                                    showFriends = false
                                    showGames = false
                                    showProfile = false
                                    Toast.makeText(ctx, "Abriendo Comunidad", Toast.LENGTH_SHORT).show()

                                },
                                onProfileClick = {
                                    showProfile = true
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    Toast.makeText(ctx, "Abriendo Perfil", Toast.LENGTH_SHORT).show()
                                },
                                onLogoutClick = {
                                    viewModel.logout()
                                    isAuthenticated = false
                                    showAuth = true
                                    showFriends = false
                                    showGames = false
                                    showCommunity = false
                                    showProfile = false
                                    Toast.makeText(ctx, "Sesion cerrada", Toast.LENGTH_SHORT).show()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
