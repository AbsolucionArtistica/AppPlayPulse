package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String,
    val email: String,
    val profilePhotoUrl: String = "",
    val highScore: Int = 0,
    val level: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
