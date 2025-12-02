package com.example.appplaypulse_grupo4.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appplaypulse_grupo4.database.entity.UserGameEntity

@Dao
interface UserGameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserGame(userGame: UserGameEntity): Long

    @Query(
        """
        SELECT * FROM user_games 
        WHERE userId = :userId 
        ORDER BY playedAt DESC 
        LIMIT :limit
        """
    )
    suspend fun getRecentGamesForUser(
        userId: Long,
        limit: Int = 10
    ): List<UserGameEntity>

    @Query("DELETE FROM user_games WHERE userId = :userId")
    suspend fun clearGamesForUser(userId: Long)
}
