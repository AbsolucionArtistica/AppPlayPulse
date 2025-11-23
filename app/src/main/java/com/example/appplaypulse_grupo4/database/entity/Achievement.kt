package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val name: String,
    val description: String = "",
    val unlockedDate: Long = System.currentTimeMillis()
)
