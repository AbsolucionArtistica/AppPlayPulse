package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_games")
data class UserGameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val gameTitle: String,
    val imageResName: String,
    val playedAt: Long = System.currentTimeMillis()
)
