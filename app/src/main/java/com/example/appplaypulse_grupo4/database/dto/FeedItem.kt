package com.example.appplaypulse_grupo4.database.dto

data class FeedItem(
    val id: Long,
    val content: String,
    val createdAt: Long,
    val username: String,
    val location: String?,
    val link: String?
)
