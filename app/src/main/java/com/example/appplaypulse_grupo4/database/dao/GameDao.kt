package com.example.appplaypulse_grupo4.database.dao

import androidx.room.*
import com.example.appplaypulse_grupo4.database.entity.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    
    @Query("SELECT * FROM games WHERE userId = :userId")
    fun getUserGames(userId: Long): Flow<List<Game>>
    
    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Long): Game?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game): Long
    
    @Update
    suspend fun updateGame(game: Game)
    
    @Delete
    suspend fun deleteGame(game: Game)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Long)
    
    @Query("SELECT COUNT(*) FROM games WHERE userId = :userId")
    suspend fun getUserGameCount(userId: Long): Int
}
