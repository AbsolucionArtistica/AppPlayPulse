package com.example.appplaypulse_grupo4.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedSideMenu() {
    var isOpen by remember { mutableStateOf(false) }

    // Animación del ancho del menú
    val menuWidth by animateDpAsState(
        targetValue = if (isOpen) 250.dp else 60.dp,
        animationSpec = tween(durationMillis = 600)
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(menuWidth)
            .background(Color.White) // Fondo blanco
    ) {
        // Botón hamburguesa / cerrar
        IconButton(
            onClick = { isOpen = !isOpen },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isOpen) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = "Toggle menu",
                tint = Color.Black, // Líneas negras
                modifier = Modifier.size(32.dp)
            )
        }

        // Contenido del menú animado
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp, top = 80.dp)
        ) {
            AnimatedVisibility(visible = isOpen) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    MenuItem("New Arrivals")
                    MenuItem("Woman")
                    MenuItem("Man")
                    MenuItem("Releases")
                    MenuItem("Stores")
                }
            }
        }
    }
}

@Composable
fun MenuItem(title: String) {
    Text(
        text = title,
        color = Color.Black, // Texto negro
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 8.dp)
    )
}
