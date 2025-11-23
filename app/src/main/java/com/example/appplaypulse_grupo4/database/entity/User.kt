package com.example.appplaypulse_grupo4.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // 🔹 Datos de cuenta
    val username: String,      // nombre de usuario
    val email: String,         // correo
    val phone: String?,        // número de teléfono (puede ser null)
    val password: String,      // por ahora en texto plano (después se puede hashear)

    // 🔹 Datos personales
    val nombre: String,
    val apellido: String,
    val edad: Int,

    // 🔹 Datos gamer / app
    val highScore: Int = 0,
    val level: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
