package com.example.appplaypulse_grupo4.api

/**
 * Modelo simple para logros individuales.
 * Ajusta campos segun lo que devuelva tu API.
 */
data class AchievementResponse(
    val id: Long,
    val title: String,
    val description: String,
    val points: Int
)

/**
 * Respuesta de lista de logros: bandera de exito y lista de logros.
 */
data class AchievementListResponse(
    val success: Boolean,
    val achievements: List<AchievementResponse>
)
