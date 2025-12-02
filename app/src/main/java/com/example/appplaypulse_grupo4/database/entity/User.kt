package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Datos personales
    val nombre: String,
    val apellido: String,
    val edad: Int,

    // Credenciales / contacto
    val email: String,
    val phone: String,      // 👈 lo usa login() y existsPhone()
    val username: String,
    val password: String,

    // Stats de juego
    val highScore: Int = 0, // 👈 lo usan getAllUsers, getTopUsers, etc.
    val level: Int = 1,     // 👈 lo usan updateUserLevel y UserSummary

    // Para orden cronológico
    val createdAt: Long = System.currentTimeMillis() // 👈 lo usa getRecentUsers
)
