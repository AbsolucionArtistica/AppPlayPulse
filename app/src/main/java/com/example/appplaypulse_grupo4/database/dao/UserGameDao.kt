package com.example.appplaypulse_grupo4.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.appplaypulse_grupo4.database.entity.UserGameEntity

@Dao
interface UserGameDao {

    @Insert
    suspend fun insertUserGame(game: UserGameEntity): Long

    @Query("SELECT * FROM user_games WHERE userId = :userId ORDER BY id DESC")
    suspend fun getRecentGamesForUser(userId: Long): List<UserGameEntity>
}
