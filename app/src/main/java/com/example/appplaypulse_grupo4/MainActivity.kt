package com.example.appplaypulse_grupo4

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appplaypulse_grupo4.database.DatabaseHelper
import com.example.appplaypulse_grupo4.ui.components.AnimatedSideMenu
import com.example.appplaypulse_grupo4.ui.screens.FriendsMockupScreen
import com.example.appplaypulse_grupo4.ui.theme.AppPlayPulse_Grupo4Theme
import com.example.appplaypulse_grupo4.ui.theme.GameManagerScreen
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
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

                var showFriends by remember { mutableStateOf(false) }
                var showGames by remember { mutableStateOf(false) }

                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 🏠 Inicio
                        if (!showFriends && !showGames) {
                            HomeScreen()
                        }

                        // 🎮 Juegos
                        if (showGames) {
                            GameManagerScreen()
                        }


                        // 🤝 Amigos
                        if (showFriends) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xAA000000))
                                    .align(Alignment.Center)
                                    .padding(24.dp)
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    tonalElevation = 4.dp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                ) {
                                    FriendsMockupScreen(onClose = { showFriends = false })
                                }
                            }
                        }

                        // 📋 Menú lateral
                        AnimatedSideMenu(
                            onHomeClick = {
                                showFriends = false
                                showGames = false
                                Toast.makeText(ctx, "Volviendo al inicio", Toast.LENGTH_SHORT).show()
                            },
                            onGamesClick = {
                                showGames = true
                                showFriends = false
                                Toast.makeText(ctx, "Abriendo Juegos", Toast.LENGTH_SHORT).show()
                            },
                            onFriendsClick = {
                                showFriends = true
                                showGames = false
                                Toast.makeText(ctx, "Abriendo Amigos", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}
