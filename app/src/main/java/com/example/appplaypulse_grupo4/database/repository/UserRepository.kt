package com.example.appplaypulse_grupo4.database.repository

import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.dto.UserSummary
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.security.DatabaseSecurityGuidelines
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
    
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    
    fun getTopUsers(limit: Int = 10): Flow<List<User>> = userDao.getTopUsers(limit)
    
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun updateUserHighScore(userId: Long, newScore: Int) {
        userDao.updateUserHighScore(userId, newScore)
    }
    
    suspend fun updateUserLevel(userId: Long, newLevel: Int) {
        userDao.updateUserLevel(userId, newLevel)
    }
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    suspend fun deleteAllUsers() = userDao.deleteAllUsers()
    
    // Business logic methods with security validation
    suspend fun createUser(username: String, email: String): Long {
        // Validate inputs before database operations
        if (!DatabaseSecurityGuidelines.isValidUsername(username)) {
            throw IllegalArgumentException("Invalid username format")
        }
        if (!DatabaseSecurityGuidelines.isValidEmail(email)) {
            throw IllegalArgumentException("Invalid email format")
        }
        
        val user = User(
            username = username,
            email = email
        )
        return insertUser(user)
    }
    
    suspend fun updateScore(userId: Long, newScore: Int) {
        val currentUser = getUserById(userId)
        currentUser?.let { user ->
            if (newScore > user.highScore) {
                updateUserHighScore(userId, newScore)
                // Level up logic (example: every 1000 points = 1 level)
                val newLevel = (newScore / 1000) + 1
                if (newLevel > user.level) {
                    updateUserLevel(userId, newLevel)
                }
            }
        }
    }
    
    // Debug methods for direct database access
    // REMOVED: executeRawQuery to prevent SQL injection
    // Use specific, parameterized methods instead
    
    suspend fun getUserStats(): Map<String, Any> {
        val userCount = userDao.getUserCount()
        val averageScore = userDao.getAverageScore()
        return mapOf(
            "totalUsers" to userCount,
            "averageScore" to averageScore,
            "databaseLocation" to "/data/data/com.example.appplaypulse_grupo4/databases/app_database"
        )
    }
    
    // Secure search methods with parameterized queries
    suspend fun searchUsersByScoreRange(minScore: Int, maxScore: Int): List<User> {
        // Validate score ranges
        val safeMinScore = maxOf(0, minScore)
        val safeMaxScore = minOf(1000000, maxScore) // Reasonable upper limit
        return userDao.getUsersInScoreRange(safeMinScore, safeMaxScore)
    }
    
    suspend fun searchUsersByUsernamePattern(pattern: String): List<User> {
        // Sanitize the pattern to prevent injection
        val safePattern = DatabaseSecurityGuidelines.sanitizeInput(pattern)
        if (safePattern.length < 2) {
            return emptyList() // Prevent too broad searches
        }
        return userDao.getUsersByUsernamePattern("%$safePattern%")
    }

    // New: recent users projection
    suspend fun getRecentUserSummaries(limit: Int): List<UserSummary> {
        return userDao.getRecentUsers(limit)
    }
}
