package com.example.appplaypulse_grupo4.database.dto

data class FeedItem(
    val id: Long,
    val username: String,
    val content: String,
    val location: String?,
    val link: String?,
    val imageUri: String?,
    val createdAt: Long
)
