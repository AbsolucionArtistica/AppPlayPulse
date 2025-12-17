package com.example.appplaypulse_grupo4.api.backend

import android.util.Log
import com.example.appplaypulse_grupo4.database.dto.FeedItem
import com.example.appplaypulse_grupo4.database.entity.User
import java.time.Instant
import com.example.appplaypulse_grupo4.ui.theme.Friend as UiFriend
import com.example.appplaypulse_grupo4.ui.screens.RecentGame as UiRecentGame

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

    suspend fun fetchUsers(): Result<List<ApiUser>> = try {
        val response = api.listUsers()
        Result.success(response.items.orEmpty())
    } catch (e: Exception) {
        Log.e("RemoteBackend", "list users error", e)
        Result.failure(e)
    }

    suspend fun fetchFriends(ownerUserId: String): Result<List<UiFriend>> = try {
        val response = api.getFriends(ownerUserId)
        val mapped = response.items.orEmpty().map { it.toUiFriend() }
        Result.success(mapped)
    } catch (e: Exception) {
        Log.e("RemoteBackend", "fetchFriends error", e)
        Result.failure(e)
    }

    suspend fun addFriend(
        ownerUserId: String,
        friendUserId: String?,
        friendName: String,
        avatarResName: String?
    ): Result<UiFriend> = try {
        val response = api.createFriend(
            CreateFriendRequest(
                ownerUserId = ownerUserId,
                friendUserId = friendUserId,
                friendName = friendName,
                avatarResName = avatarResName,
                isOnline = true
            )
        )
        response.item?.let { Result.success(it.toUiFriend()) }
            ?: Result.failure(Exception("No se pudo crear amigo remoto"))
    } catch (e: Exception) {
        Log.e("RemoteBackend", "addFriend error", e)
        Result.failure(e)
    }

    suspend fun fetchGames(userId: String): Result<List<UiRecentGame>> = try {
        val response = api.getGames(userId)
        val mapped = response.items.orEmpty().map { it.toUiRecentGame() }
        Result.success(mapped)
    } catch (e: Exception) {
        Log.e("RemoteBackend", "fetchGames error", e)
        Result.failure(e)
    }

    suspend fun addGame(
        userId: String,
        gameTitle: String,
        imageResName: String?
    ): Result<UiRecentGame> = try {
        val response = api.createGame(
            CreateGameRequest(
                userId = userId,
                gameTitle = gameTitle,
                imageResName = imageResName
            )
        )
        response.item?.let { Result.success(it.toUiRecentGame()) }
            ?: Result.failure(Exception("No se pudo crear juego remoto"))
    } catch (e: Exception) {
        Log.e("RemoteBackend", "addGame error", e)
        Result.failure(e)
    }

    suspend fun deleteUser(userId: String): Result<Unit> = try {
        val response = api.deleteUser(userId)
        if (response.deleted == true) {
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message ?: "No se pudo eliminar usuario"))
        }
    } catch (e: Exception) {
        Log.e("RemoteBackend", "deleteUser error", e)
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

private fun ApiFriend.toUiFriend(): UiFriend {
    return UiFriend(
        name = friendName,
        profileRes = 0,
        gameName = "Jugando ahora",
        gameImageRes = 0,
        hours = "",
        isOnline = isOnline == true,
        avatarResName = avatarResName
    )
}

private fun ApiGame.toUiRecentGame(): UiRecentGame {
    return UiRecentGame(
        title = gameTitle,
        imageRes = 0
    )
}
