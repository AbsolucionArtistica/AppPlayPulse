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
        link: String?
    ) {
        val post = Post(
            userId = userId,
            content = content,
            location = location?.takeIf { it.isNotBlank() },
            link = link?.takeIf { it.isNotBlank() }
        )
        postDao.insertPost(post)
    }
}
