package com.example.appplaypulse_grupo4.ui.screens.models

data class PollPost(
    val id: Long = 0,
    val userName: String,
    val userAvatar: String?, // ruta de imagen o null
    val question: String,
    val options: List<String>,
    val votes: MutableList<Int> = MutableList(options.size) { 0 },
    val timestamp: Long = System.currentTimeMillis()
)
