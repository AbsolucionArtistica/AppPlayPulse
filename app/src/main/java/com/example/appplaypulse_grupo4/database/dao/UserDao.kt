package com.example.appplaypulse_grupo4.database.dao

import androidx.room.*
import com.example.appplaypulse_grupo4.database.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users ORDER BY highScore DESC")
    fun getAllUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users ORDER BY highScore DESC LIMIT :limit")
    fun getTopUsers(limit: Int): Flow<List<User>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("UPDATE users SET highScore = :newScore WHERE id = :userId")
    suspend fun updateUserHighScore(userId: Long, newScore: Int)
    
    @Query("UPDATE users SET level = :newLevel WHERE id = :userId")
    suspend fun updateUserLevel(userId: Long, newLevel: Int)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    // Debug and custom query methods
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("SELECT AVG(highScore) FROM users")
    suspend fun getAverageScore(): Double
    
    @Query("SELECT * FROM users WHERE highScore BETWEEN :minScore AND :maxScore")
    suspend fun getUsersInScoreRange(minScore: Int, maxScore: Int): List<User>
    
    @Query("SELECT * FROM users WHERE username LIKE :pattern")
    suspend fun getUsersByUsernamePattern(pattern: String): List<User>
    
    @Query("SELECT * FROM users WHERE highScore > :minScore")
    suspend fun getUsersAboveScore(minScore: Int): List<User>
    
    @Query("SELECT username, highScore, level FROM users ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentUsers(limit: Int): List<User>
}
