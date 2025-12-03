package com.example.appplaypulse_grupo4.api.backend

import com.google.gson.annotations.SerializedName

data class ApiUser(
    @SerializedName("id")
    val id: String,
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val email: String,
    val phone: String,
    val username: String,
    val createdAt: String?
)

data class AuthResponse(
    val user: ApiUser?
)

data class LoginRequest(
    val field: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val email: String,
    val phone: String,
    val username: String,
    val password: String
)

data class ApiPostItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    val username: String,
    val content: String,
    val location: String?,
    val link: String?,
    val imageUri: String?,
    val createdAt: String?
)

data class PostsResponse(
    val items: List<ApiPostItem>?
)

data class CreatePostRequest(
    val userId: String,
    val username: String,
    val content: String,
    val location: String?,
    val link: String?,
    val imageUri: String?
)

data class CreatePostResponse(
    val item: ApiPostItem?
)
