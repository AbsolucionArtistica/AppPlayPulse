package com.example.appplaypulse_grupo4.ui.screens.models

data class ImagePost(
    val id: Long = 0,
    val userName: String,
    val userAvatar: String?, // ruta de imagen o null
    val text: String,
    val imageUri: String, // URI de la galería
    val timestamp: Long = System.currentTimeMillis()
)
