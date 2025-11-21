package com.example.appplaypulse_grupo4.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============================================
    // USER ENDPOINTS (4)
    // ============================================

    @POST("api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Long): Response<UserResponse>

    @PUT("api/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Long,
        @Body request: UpdateUserRequest
    ): Response<ApiResponse>

    // ============================================
    // GAME ENDPOINTS (5)
    // ============================================

    @GET("api/users/{userId}/games")
    suspend fun getUserGames(@Path("userId") userId: Long): Response<GameListResponse>

    @POST("api/games")
    suspend fun createGame(
        @Query("userId") userId: Long,
        @Body request: CreateGameRequest
    ): Response<GameResponse>

    @PUT("api/games/{gameId}")
    suspend fun updateGame(
        @Path("gameId") gameId: Long,
        @Body request: UpdateGameRequest
    ): Response<ApiResponse>

    @DELETE("api/games/{gameId}")
    suspend fun deleteGame(@Path("gameId") gameId: Long): Response<ApiResponse>

    @GET("api/health")
    suspend fun checkHealth(): Response<ApiResponse>

    // ============================================
    // ACHIEVEMENT ENDPOINTS (3)
    // ============================================

    @GET("api/games/{gameId}/achievements")
    suspend fun getGameAchievements(@Path("gameId") gameId: Long): Response<AchievementListResponse>

    @POST("api/achievements")
    suspend fun createAchievement(
        @Body request: CreateAchievementRequest
    ): Response<AchievementResponse>

    @DELETE("api/achievements/{achievementId}")
    suspend fun deleteAchievement(@Path("achievementId") achievementId: Long): Response<ApiResponse>

}
