package com.example.appplaypulse_grupo4.database.repository

import com.example.appplaypulse_grupo4.database.dao.PostDao
import com.example.appplaypulse_grupo4.database.entity.Post

class PostRepository(
    private val postDao: PostDao
) {

    fun getFeed() = postDao.getFeed()

    suspend fun addPost(
        userId: Long,
        content: String,
        location: String?,
        link: String?,
        imageUri: String?
    ) {
        val post = Post(
            userId = userId,
            content = content,
            location = location,
            link = link,
            imageUri = imageUri
        )
        postDao.insertPost(post)
    }

    suspend fun clearAll() {
        postDao.clearAll()
    }
}
