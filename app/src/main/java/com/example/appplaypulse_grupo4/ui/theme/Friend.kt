package com.example.appplaypulse_grupo4.ui.theme

/**
 * Modelo de amigo usado SOLO para la UI.
 * No es la entidad de base de datos (esa es FriendEntity).
 */
data class Friend(
    val name: String,
    val profileRes: Int,
    val gameName: String,
    val gameImageRes: Int,
    val hours: String,
    val isOnline: Boolean,
    val avatarResName: String? = null
)
