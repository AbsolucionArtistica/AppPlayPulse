package com.example.appplaypulse_grupo4.database.repository

import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(
        nombre: String,
        apellido: String,
        edad: Int,
        email: String,
        phone: String,
        username: String,
        password: String
    ): Result<User> {

        if (userDao.existsUsername(username) != null)
            return Result.failure(Exception("El nombre de usuario ya existe"))

        if (userDao.existsEmail(email) != null)
            return Result.failure(Exception("El correo ya está registrado"))

        if (phone.isNotBlank() && userDao.existsPhone(phone) != null)
            return Result.failure(Exception("El teléfono ya está registrado"))

        val user = User(
            username = username,
            email = email,
            phone = phone,
            password = password,
            nombre = nombre,
            apellido = apellido,
            edad = edad
        )

        val id = userDao.insertUser(user)

        return if (id != -1L)
            Result.success(user.copy(id = id))
        else
            Result.failure(Exception("Error al crear usuario"))
    }

    suspend fun login(field: String, password: String): Result<User> =
        userDao.login(field, password)?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Usuario o contraseña incorrectos"))

    // 👤 Actualizar username
    suspend fun updateUsername(userId: Long, newUser: String): Result<User> {
        val exists = userDao.existsUsername(newUser)
        if (exists != null && exists.id != userId) {
            return Result.failure(Exception("Ese usuario ya existe"))
        }

        userDao.updateUsername(userId, newUser)
        val updated = userDao.getUserById(userId)
            ?: return Result.failure(Exception("Usuario no encontrado"))

        return Result.success(updated)
    }
}
