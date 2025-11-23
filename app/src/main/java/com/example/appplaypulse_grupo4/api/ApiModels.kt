package com.example.appplaypulse_grupo4.api

import com.google.gson.annotations.SerializedName

// ============================================
// USER MODELS
// ============================================

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String
)

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class UserResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("profilePhotoUrl")
    val profilePhotoUrl: String,
    @SerializedName("highScore")
    val highScore: Int,
    @SerializedName("level")
    val level: Int,
    @SerializedName("createdAt")
    val createdAt: Long
)

data class UpdateUserRequest(
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("profilePhotoUrl")
    val profilePhotoUrl: String? = null,
    @SerializedName("highScore")
    val highScore: Int? = null,
    @SerializedName("level")
    val level: Int? = null
)

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: UserResponse? = null
)

// ============================================
// GAME MODELS
// ============================================

data class GameResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("addedDate")
    val addedDate: Long
)

data class CreateGameRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String = "",
    @SerializedName("score")
    val score: Int = 0
)

data class UpdateGameRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("photoUrl")
    val photoUrl: String? = null,
    @SerializedName("score")
    val score: Int? = null
)

data class GameListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("games")
    val games: List<GameResponse>
)

// ============================================
// ACHIEVEMENT MODELS
// ============================================

data class AchievementResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("gameId")
    val gameId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("unlockedDate")
    val unlockedDate: Long
)

data class CreateAchievementRequest(
    @SerializedName("gameId")
    val gameId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String = ""
)

data class AchievementListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("achievements")
    val achievements: List<AchievementResponse>
)

// ============================================
// GENERIC RESPONSE
// ============================================

data class ApiResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)
