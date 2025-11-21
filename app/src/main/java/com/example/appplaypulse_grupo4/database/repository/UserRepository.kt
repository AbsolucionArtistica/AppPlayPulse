package com.example.appplaypulse_grupo4.database.repository

import com.example.appplaypulse_grupo4.api.RetrofitClientManager
import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.dao.GameDao
import com.example.appplaypulse_grupo4.database.dao.AchievementDao
import com.example.appplaypulse_grupo4.database.dto.UserSummary
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.entity.Game
import com.example.appplaypulse_grupo4.database.entity.Achievement
import com.example.appplaypulse_grupo4.security.DatabaseSecurityGuidelines
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val gameDao: GameDao,
    private val achievementDao: AchievementDao
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
    suspend fun createUser(username: String, email: String, password: String = "default_pass"): Long {
        // Validate inputs before database operations
        if (!DatabaseSecurityGuidelines.isValidUsername(username)) {
            throw IllegalArgumentException("Invalid username format")
        }
        if (!DatabaseSecurityGuidelines.isValidEmail(email)) {
            throw IllegalArgumentException("Invalid email format")
        }
        
        val user = User(
            username = username,
            password = password,
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

    // ============================================
    // GAME OPERATIONS
    // ============================================

    fun getUserGames(userId: Long): Flow<List<Game>> = gameDao.getUserGames(userId)

    suspend fun addGame(userId: Long, name: String, photoUrl: String = "", score: Int = 0): Long {
        val game = Game(
            userId = userId,
            name = name,
            photoUrl = photoUrl,
            score = score
        )
        return gameDao.insertGame(game)
    }

    suspend fun updateGame(gameId: Long, name: String? = null, photoUrl: String? = null, score: Int? = null) {
        val game = gameDao.getGameById(gameId) ?: return
        val updated = game.copy(
            name = name ?: game.name,
            photoUrl = photoUrl ?: game.photoUrl,
            score = score ?: game.score
        )
        gameDao.updateGame(updated)
    }

    suspend fun deleteGame(gameId: Long) {
        gameDao.deleteGameById(gameId)
    }

    suspend fun getGameById(gameId: Long): Game? = gameDao.getGameById(gameId)

    // ============================================
    // ACHIEVEMENT OPERATIONS
    // ============================================

    fun getGameAchievements(gameId: Long): Flow<List<Achievement>> = achievementDao.getAchievementsByGame(gameId)

    suspend fun addAchievement(gameId: Long, name: String, description: String = ""): Long {
        val achievement = Achievement(
            gameId = gameId,
            name = name,
            description = description
        )
        return achievementDao.insertAchievement(achievement)
    }

    suspend fun deleteAchievement(achievementId: Long) {
        achievementDao.deleteAchievementById(achievementId)
    }

    suspend fun getAchievementById(achievementId: Long): Achievement? = achievementDao.getAchievementById(achievementId)

    // ============================================
    // SYNC WITH SERVER
    // ============================================

    /**
     * Sincroniza un usuario registrado con el servidor backend
     */
    suspend fun syncUserWithServer(username: String, password: String, email: String): Result<User> {
        return try {
            val registerRequest = com.example.appplaypulse_grupo4.api.RegisterRequest(
                username = username,
                password = password,
                email = email
            )
            val response = RetrofitClientManager.apiService.registerUser(registerRequest)
            val authResponse = response.body()
            
            if (response.isSuccessful && authResponse != null && authResponse.user != null) {
                val userResponse = authResponse.user
                val localUser = User(
                    id = userResponse.id,
                    username = userResponse.username,
                    password = password,  // Store locally
                    email = userResponse.email,
                    profilePhotoUrl = userResponse.profilePhotoUrl,
                    highScore = userResponse.highScore,
                    level = userResponse.level,
                    createdAt = userResponse.createdAt
                )
                insertUser(localUser)
                Result.success(localUser)
            } else {
                Result.failure(Exception("Failed to register: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza un usuario con el servidor para login
     */
    suspend fun loginUserWithServer(username: String, password: String): Result<User> {
        return try {
            val loginRequest = com.example.appplaypulse_grupo4.api.LoginRequest(
                username = username,
                password = password
            )
            val response = RetrofitClientManager.apiService.loginUser(loginRequest)
            val authResponse = response.body()
            
            if (response.isSuccessful && authResponse != null && authResponse.user != null) {
                val userResponse = authResponse.user
                val localUser = User(
                    id = userResponse.id,
                    username = userResponse.username,
                    password = password,
                    email = userResponse.email,
                    profilePhotoUrl = userResponse.profilePhotoUrl,
                    highScore = userResponse.highScore,
                    level = userResponse.level,
                    createdAt = userResponse.createdAt
                )
                // Check if user already exists locally
                val existingUser = getUserById(userResponse.id)
                if (existingUser != null) {
                    updateUser(localUser)
                } else {
                    insertUser(localUser)
                }
                Result.success(localUser)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza los juegos del usuario desde el servidor
     */
    suspend fun syncGamesFromServer(userId: Long): Result<List<Game>> {
        return try {
            val response = RetrofitClientManager.apiService.getUserGames(userId)
            val gameListResponse = response.body()
            
            if (response.isSuccessful && gameListResponse != null) {
                val gameResponses = gameListResponse.games
                val localGames = gameResponses.map { gameResponse ->
                    Game(
                        id = gameResponse.id,
                        userId = gameResponse.userId,
                        name = gameResponse.name,
                        photoUrl = gameResponse.photoUrl,
                        score = gameResponse.score,
                        addedDate = gameResponse.addedDate
                    )
                }
                // Clear existing games and insert new ones
                localGames.forEach { gameDao.insertGame(it) }
                Result.success(localGames)
            } else {
                Result.failure(Exception("Failed to fetch games: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza los logros de un juego desde el servidor
     */
    suspend fun syncAchievementsFromServer(gameId: Long): Result<List<Achievement>> {
        return try {
            val response = RetrofitClientManager.apiService.getGameAchievements(gameId)
            val achievementListResponse = response.body()
            
            if (response.isSuccessful && achievementListResponse != null) {
                val achievementResponses = achievementListResponse.achievements
                val localAchievements = achievementResponses.map { achResponse ->
                    Achievement(
                        id = achResponse.id,
                        gameId = achResponse.gameId,
                        name = achResponse.name,
                        description = achResponse.description,
                        unlockedDate = achResponse.unlockedDate
                    )
                }
                localAchievements.forEach { achievementDao.insertAchievement(it) }
                Result.success(localAchievements)
            } else {
                Result.failure(Exception("Failed to fetch achievements: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
