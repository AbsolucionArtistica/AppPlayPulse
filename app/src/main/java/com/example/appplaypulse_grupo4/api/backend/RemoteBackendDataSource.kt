package com.example.appplaypulse_grupo4.api.backend

import android.util.Log
import com.example.appplaypulse_grupo4.database.dto.FeedItem
import com.example.appplaypulse_grupo4.database.entity.User
import java.time.Instant

class RemoteBackendDataSource(
    private val api: BackendApi
) {
    suspend fun login(field: String, password: String): Result<ApiUser> = try {
        val response = api.login(LoginRequest(field = field, password = password))
        response.user?.let { Result.success(it) }
            ?: Result.failure(Exception("Respuesta sin usuario"))
    } catch (e: Exception) {
        Log.e("RemoteBackend", "login error", e)
        Result.failure(e)
    }

    suspend fun register(user: User, password: String): Result<ApiUser> = try {
        val response = api.register(
            RegisterRequest(
                nombre = user.nombre,
                apellido = user.apellido,
                edad = user.edad,
                email = user.email,
                phone = user.phone,
                username = user.username,
                password = password
            )
        )
        response.user?.let { Result.success(it) }
            ?: Result.failure(Exception("Respuesta sin usuario"))
    } catch (e: Exception) {
        Log.e("RemoteBackend", "register error", e)
        Result.failure(e)
    }

    suspend fun ensureUser(user: User, password: String): Result<ApiUser> {
        val loginAttempt = login(user.username, password)
        if (loginAttempt.isSuccess) return loginAttempt

        return register(user, password)
    }

    suspend fun fetchFeed(): Result<List<FeedItem>> = try {
        val response = api.getPosts()
        val items = response.items.orEmpty().map { it.toFeedItem() }
        Result.success(items)
    } catch (e: Exception) {
        Log.e("RemoteBackend", "fetchFeed error", e)
        Result.failure(e)
    }

    suspend fun publishPost(
        remoteUserId: String,
        username: String,
        content: String,
        location: String?,
        link: String?,
        imageUri: String?
    ): Result<FeedItem> = try {
        val response = api.createPost(
            CreatePostRequest(
                userId = remoteUserId,
                username = username,
                content = content,
                location = location,
                link = link,
                imageUri = imageUri
            )
        )
        response.item?.let { Result.success(it.toFeedItem()) }
            ?: Result.failure(Exception("No se pudo crear el post remoto"))
    } catch (e: Exception) {
        Log.e("RemoteBackend", "publishPost error", e)
        Result.failure(e)
    }
}

private fun ApiPostItem.toFeedItem(): FeedItem {
    val created = try {
        createdAt?.let { Instant.parse(it).toEpochMilli() }
    } catch (_: Exception) {
        null
    }

    return FeedItem(
        id = created ?: System.currentTimeMillis(),
        username = username,
        content = content,
        location = location,
        link = link,
        imageUri = imageUri,
        createdAt = created ?: System.currentTimeMillis()
    )
}
