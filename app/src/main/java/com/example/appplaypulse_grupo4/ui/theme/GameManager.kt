package com.example.appplaypulse_grupo4.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameInputScreen(gameList: MutableList<String>) {
    var gameName by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicTextField(
            value = gameName,
            onValueChange = { gameName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.padding(8.dp)) {
                    if (gameName.isEmpty()) {
                        Text("Ingresa el nombre del juego")
                    }
                    innerTextField()
                }
            }
        )
        Button(
            onClick = {
                if (gameName.isNotBlank()) {
                    gameList.add(gameName)
                    gameName = ""
                }
            }
        ) {
            Text("Agregar juego")
        }
    }
}

@Composable
fun GameListScreen(gameList: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Tus juegos:", style = MaterialTheme.typography.titleMedium)
        for ((index, game) in gameList.withIndex()) {
            Text("${index + 1}. $game")
        }
    }
}
