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

data class ApiFriend(
    @SerializedName("id")
    val id: String,
    @SerializedName("ownerUserId")
    val ownerUserId: String,
    @SerializedName("friendUserId")
    val friendUserId: String?,
    @SerializedName("friendName")
    val friendName: String,
    @SerializedName("avatarResName")
    val avatarResName: String?,
    @SerializedName("isOnline")
    val isOnline: Boolean?,
    @SerializedName("friendSince")
    val friendSince: String?
)

data class FriendsResponse(
    val items: List<ApiFriend>?
)

data class CreateFriendRequest(
    val ownerUserId: String,
    val friendUserId: String?,
    val friendName: String,
    val avatarResName: String?,
    val isOnline: Boolean?
)

data class CreateFriendResponse(
    val item: ApiFriend?
)

data class ApiGame(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("gameTitle")
    val gameTitle: String,
    @SerializedName("imageResName")
    val imageResName: String?,
    @SerializedName("playedAt")
    val playedAt: String?
)

data class GamesResponse(
    val items: List<ApiGame>?
)

data class CreateGameRequest(
    val userId: String,
    val gameTitle: String,
    val imageResName: String?
)

data class CreateGameResponse(
    val item: ApiGame?
)

data class UsersListResponse(
    val items: List<ApiUser>?
)

data class DeleteUserResponse(
    val deleted: Boolean?,
    val message: String? = null
)
