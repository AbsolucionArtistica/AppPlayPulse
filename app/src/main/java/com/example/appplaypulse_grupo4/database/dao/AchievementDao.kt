package com.example.appplaypulse_grupo4.database.dao

import androidx.room.*
import com.example.appplaypulse_grupo4.database.entity.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    
    @Query("SELECT * FROM achievements WHERE gameId = :gameId")
    fun getGameAchievements(gameId: Long): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE gameId = :gameId")
    fun getAchievementsByGame(gameId: Long): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    suspend fun getAchievementById(achievementId: Long): Achievement?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long
    
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    @Delete
    suspend fun deleteAchievement(achievement: Achievement)

    @Query("DELETE FROM achievements WHERE id = :achievementId")
    suspend fun deleteAchievementById(achievementId: Long)
    
    @Query("SELECT COUNT(*) FROM achievements WHERE gameId = :gameId")
    suspend fun getGameAchievementCount(gameId: Long): Int
}
