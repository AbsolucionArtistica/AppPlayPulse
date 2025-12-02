package com.example.appplaypulse_grupo4.database.repository

import com.example.appplaypulse_grupo4.database.dao.UserGameDao
import com.example.appplaypulse_grupo4.database.entity.UserGameEntity

class UserGameRepository(
    private val userGameDao: UserGameDao
) {

    // Registrar un juego jugado por el usuario
    suspend fun addUserGame(
        userId: Long,
        gameTitle: String,
        imageResName: String
    ) {
        val entity = UserGameEntity(
            userId = userId,
            gameTitle = gameTitle,
            imageResName = imageResName
        )
        userGameDao.insertUserGame(entity)
    }

    // Obtener juegos recientes del usuario
    suspend fun getRecentGames(userId: Long, limit: Int = 10): List<UserGameEntity> {
        return userGameDao.getRecentGamesForUser(userId, limit)
    }

    // Borrar juegos recientes del usuario (por si limpias historial)
    suspend fun clearForUser(userId: Long) {
        userGameDao.clearGamesForUser(userId)
    }
}
