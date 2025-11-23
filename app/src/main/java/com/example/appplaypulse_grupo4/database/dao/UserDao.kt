package com.example.appplaypulse_grupo4.database.dao

import androidx.room.*
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.dto.UserSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ==============================
    // 🔐 LOGIN Y REGISTRO
    // ==============================

    // Login con username O email O phone + password
    @Query("""
        SELECT * FROM users
        WHERE (username = :field OR email = :field OR phone = :field)
          AND password = :password
        LIMIT 1
    """)
    suspend fun login(field: String, password: String): User?

    // ¿Ya existe el username?
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun existsUsername(username: String): User?

    // ¿Ya existe el correo?
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun existsEmail(email: String): User?

    // ¿Ya existe el teléfono?
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun existsPhone(phone: String): User?

    // ==============================
    // 🔹 CRUD PRINCIPAL
    // ==============================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    // Obtener usuario por id (SOLO UNA VEZ, sin duplicar)
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users ORDER BY highScore DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users ORDER BY highScore DESC LIMIT :limit")
    fun getTopUsers(limit: Int): Flow<List<User>>

    // ==============================
    // 🔹 ACTUALIZACIÓN DE CAMPOS
    // ==============================

    // Para cambiar nombre de usuario desde el Perfil
    @Query("UPDATE users SET username = :username WHERE id = :id")
    suspend fun updateUsername(id: Long, username: String)

    @Query("UPDATE users SET highScore = :newScore WHERE id = :userId")
    suspend fun updateUserHighScore(userId: Long, newScore: Int)

    @Query("UPDATE users SET level = :newLevel WHERE id = :userId")
    suspend fun updateUserLevel(userId: Long, newLevel: Int)

    // ==============================
    // 🔹 ESTADÍSTICAS / LISTADOS
    // ==============================

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
    suspend fun getRecentUsers(limit: Int): List<UserSummary>

    @Query("""
    SELECT * FROM users
    WHERE id != :currentUserId
    AND username NOT IN (
        SELECT name FROM friends
        WHERE ownerUserId = :currentUserId
    )
""")
    suspend fun getSuggestedFriends(currentUserId: Long): List<User>
}
