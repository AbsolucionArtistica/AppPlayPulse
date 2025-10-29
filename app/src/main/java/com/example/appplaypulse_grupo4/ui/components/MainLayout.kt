package com.example.appplaypulse_grupo4.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainLayout(
    onHomeClick: (() -> Unit)? = null,
    onGamesClick: (() -> Unit)? = null,
    onFriendsClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 🧭 Menú lateral (Sidebar animado)
        AnimatedSideMenu(
            onHomeClick = onHomeClick,
            onGamesClick = onGamesClick,
            onFriendsClick = onFriendsClick
        )

        // 🧱 Contenido de la pantalla (encima del menú)
        content()
    }
}
