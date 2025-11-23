package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,           // "Apex Legends"
    val imageResName: String     // "apex", "arena", "finalfantasy" (nombre del drawable)
)
