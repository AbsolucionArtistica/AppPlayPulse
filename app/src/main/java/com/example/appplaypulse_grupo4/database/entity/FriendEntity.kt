package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val ownerUserId: Long,        // a qué usuario pertenece este amigo

    val name: String,             // "Nuggw"
    val avatarResName: String,    // "giphy", "agua", "elena"
    val isOnline: Boolean = false
)
