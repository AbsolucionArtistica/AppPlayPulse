package com.example.appplaypulse_grupo4.database.repository

import com.example.appplaypulse_grupo4.database.dao.UserGameDao
import com.example.appplaypulse_grupo4.database.entity.UserGameEntity

class UserGameRepository(
    private val userGameDao: UserGameDao
) {

    /**
     * Agrega un juego a un usuario.
     * Como tu UserGameEntity guarda directamente el título + nombre del drawable,
     * usamos esos campos (NO gameId / hoursPlayed / isRecent).
     */
    suspend fun addGameToUser(
        userId: Long,
        gameTitle: String,
        imageResName: String
    ): Result<Unit> {
        val userGame = UserGameEntity(
            userId = userId,
            gameTitle = gameTitle,
            imageResName = imageResName
        )

        userGameDao.insertUserGame(userGame)
        return Result.success(Unit)
    }

    /**
     * Devuelve los juegos recientes del usuario.
     * Tu DAO seguramente tiene algo como:
     *  suspend fun getRecentGamesForUser(userId: Long): List<UserGameEntity>
     */
    suspend fun getRecentGamesForUser(userId: Long): List<UserGameEntity> {
        return userGameDao.getRecentGamesForUser(userId)
    }
}
