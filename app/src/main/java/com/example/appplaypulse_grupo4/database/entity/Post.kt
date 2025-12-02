package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val content: String,
    val location: String?,
    val link: String?,
    val imageUri: String?,
    val createdAt: Long = System.currentTimeMillis()
)
