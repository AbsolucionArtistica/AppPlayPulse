package com.example.appplaypulse_grupo4.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedSideMenu(
    onHomeClick: (() -> Unit)? = null,
    onGamesClick: (() -> Unit)? = null,
    onFriendsClick: (() -> Unit)? = null,
    onCommunityClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onUserManagementClick: (() -> Unit)? = null,
    showUserManagement: Boolean = false
) {
    var isOpen by remember { mutableStateOf(false) }
    val menuWidth = 220.dp

    val offsetX by animateDpAsState(
        targetValue = if (isOpen) 0.dp else -menuWidth,
        animationSpec = tween(durationMillis = 400),
        label = "menuSlide"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo semitransparente al abrir el menu
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x55000000))
                    .clickable { isOpen = false }
            )
        }

        // Panel lateral
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(menuWidth)
                .offset(x = offsetX)
                .background(Color(0xFFEEF1F5))
        ) {
            AnimatedVisibility(
                visible = isOpen,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, top = 80.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    MenuItem("Inicio") {
                        isOpen = false
                        onHomeClick?.invoke()
                    }
                    MenuItem("Juegos") {
                        isOpen = false
                        onGamesClick?.invoke()
                    }
                    MenuItem("Amigos") {
                        isOpen = false
                        onFriendsClick?.invoke()
                    }
                    MenuItem("Comunidad") {
                        isOpen = false
                        onCommunityClick?.invoke()
                    }
                    if (showUserManagement) {
                        MenuItem("Gestion de usuarios") {
                            isOpen = false
                            onUserManagementClick?.invoke()
                        }
                    }
                    MenuItem("Perfil") {
                        isOpen = false
                        onProfileClick?.invoke()
                    }
                }
            }
        }

        // Boton hamburguesa
        IconButton(
            onClick = { isOpen = !isOpen },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isOpen) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = "Toggle menu",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun MenuItem(text: String, onClick: (() -> Unit)? = null) {
    Text(
        text = text,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick?.invoke() }
    )
}
