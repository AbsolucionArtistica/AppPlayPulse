package com.example.appplaypulse_grupo4.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.appplaypulse_grupo4.database.entity.GameEntity

@Dao
interface GameDao {

    @Query("SELECT * FROM games")
    suspend fun getAllGames(): List<GameEntity>

    @Insert
    suspend fun insertGames(games: List<GameEntity>)

    @Query("SELECT * FROM games WHERE title = :title LIMIT 1")
    suspend fun getGameByTitle(title: String): GameEntity?
}
