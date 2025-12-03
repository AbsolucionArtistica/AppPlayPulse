package com.example.appplaypulse_grupo4.api.backend

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("/api/posts")
    suspend fun getPosts(): PostsResponse

    @POST("/api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): CreatePostResponse

    @GET("/api/users")
    suspend fun listUsers(): UsersListResponse

    @GET("/api/friends")
    suspend fun getFriends(@Query("ownerUserId") ownerUserId: String): FriendsResponse

    @POST("/api/friends")
    suspend fun createFriend(@Body request: CreateFriendRequest): CreateFriendResponse

    @GET("/api/games")
    suspend fun getGames(@Query("userId") userId: String): GamesResponse

    @POST("/api/games")
    suspend fun createGame(@Body request: CreateGameRequest): CreateGameResponse

    @GET("/health")
    suspend fun health(): Map<String, Any>
}
