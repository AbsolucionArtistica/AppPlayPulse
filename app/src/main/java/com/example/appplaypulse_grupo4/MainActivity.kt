package com.example.appplaypulse_grupo4

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appplaypulse_grupo4.database.DatabaseHelper
import com.example.appplaypulse_grupo4.ui.components.AnimatedSideMenu
import com.example.appplaypulse_grupo4.ui.screens.AuthScreen
import com.example.appplaypulse_grupo4.ui.screens.FriendsMockupScreen
import com.example.appplaypulse_grupo4.ui.screens.SocialFeedScreen
import com.example.appplaypulse_grupo4.ui.theme.AppPlayPulse_Grupo4Theme
import com.example.appplaypulse_grupo4.ui.theme.GameManagerScreen
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
import com.viewmodel.MainViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.appplaypulse_grupo4.ui.screens.ProfileScreen


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

                // 🔐 Estado de autenticación
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }
                var showAuth by rememberSaveable { mutableStateOf(true) } // ← arranca mostrando Auth

                // Estados de navegación interna (solo se usan cuando ya estás autenticado)
                var showFriends by remember { mutableStateOf(false) }
                var showGames by remember { mutableStateOf(false) }
                var showCommunity by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }

                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 🔐 Si NO está autenticado, siempre muestra AuthScreen
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
                                }
                            )
                        } else {
                            // 🏠 Inicio
                            if (!showFriends && !showGames && !showCommunity) {
                                HomeScreen()
                            }

                            // 👥 Amigos
                            if (showFriends) {
                                FriendsMockupScreen(onClose = { showFriends = false })
                            }

                            // 🎮 Juegos
                            if (showGames) {
                                GameManagerScreen()
                            }
                            if (showProfile) {
                                ProfileScreen(onClose = { showProfile = false })
                            }

                            // 💬 Comunidad
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

                            // 🎛️ Menú lateral animado (solo si estás autenticado)
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
                                onProfileClick = { // ✅ NUEVO
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
