package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val photoUrl: String = "",
    val score: Int = 0,
    val addedDate: Long = System.currentTimeMillis()
)
