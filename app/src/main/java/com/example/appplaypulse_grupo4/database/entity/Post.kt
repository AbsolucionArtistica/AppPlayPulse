package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,                 // id del User que la escribió
    val content: String,              // texto del post
    val location: String? = null,     // opcional
    val link: String? = null,         // opcional
    val createdAt: Long = System.currentTimeMillis()
)
