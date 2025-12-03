package com.example.appplaypulse_grupo4.api.backend

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("/api/posts")
    suspend fun getPosts(): PostsResponse

    @POST("/api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): CreatePostResponse

    @GET("/health")
    suspend fun health(): Map<String, Any>
}
