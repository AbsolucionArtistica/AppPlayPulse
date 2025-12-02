package com.example.appplaypulse_grupo4.database.repository

import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.entity.User

class UserRepository(
    private val userDao: UserDao
) {

    // LOGIN: field puede ser username / email / phone
    suspend fun login(field: String, password: String): Result<User> {
        return try {
            val user = userDao.login(field, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario o contrasena incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REGISTRO
    suspend fun registerUser(
        nombre: String,
        apellido: String,
        edad: Int,
        email: String,
        phone: String,
        username: String,
        password: String
    ): Result<User> {
        return try {
            val trimmedEmail = email.trim()
            val trimmedPhone = phone.trim()
            val trimmedUsername = username.trim()
            val passwordValue = password.trim()

            // Validaciones basicas
            if (trimmedUsername.isBlank() || passwordValue.isBlank()) {
                return Result.failure(Exception("Usuario y contrasena son obligatorios"))
            }

            if (edad < 12) {
                return Result.failure(Exception("Debes tener al menos 12 anos"))
            }

            if (!trimmedEmail.contains("@")) {
                return Result.failure(Exception("Correo invalido"))
            }

            // +56 9 ########
            val phoneRegex = Regex("^\\+56\\s?9\\d{8}$")
            if (!phoneRegex.matches(trimmedPhone)) {
                return Result.failure(Exception("Telefono invalido. Usa +56 9 XXXXXXXX"))
            }

            // 8+ caracteres, 1 mayuscula, 1 minuscula, 1 numero
            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
            if (!passwordRegex.matches(passwordValue)) {
                return Result.failure(
                    Exception("La contrasena debe tener 8 caracteres, 1 mayuscula, 1 minuscula y numero")
                )
            }

            // Ya existe username?
            if (userDao.existsUsername(trimmedUsername) != null) {
                return Result.failure(Exception("El nombre de usuario ya existe"))
            }

            // Ya existe email?
            if (userDao.existsEmail(trimmedEmail) != null) {
                return Result.failure(Exception("El correo ya esta registrado"))
            }

            // Ya existe telefono?
            if (userDao.existsPhone(trimmedPhone) != null) {
                return Result.failure(Exception("El telefono ya esta registrado"))
            }

            val newUser = User(
                nombre = nombre,
                apellido = apellido,
                edad = edad,
                email = trimmedEmail,
                phone = trimmedPhone,
                username = trimmedUsername,
                password = passwordValue
            )

            val id = userDao.insertUser(newUser)
            val inserted = newUser.copy(id = id)

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CAMBIAR USERNAME DESDE PERFIL
    suspend fun updateUsername(
        userId: Long,
        newUsername: String
    ): Result<User> {
        return try {
            if (newUsername.isBlank()) {
                return Result.failure(Exception("El nombre de usuario no puede estar vacio"))
            }

            // revisar que no exista otro usuario con ese username
            val existing = userDao.existsUsername(newUsername)
            if (existing != null && existing.id != userId) {
                return Result.failure(Exception("Ese nombre de usuario ya esta en uso"))
            }

            // actualizar en BD
            userDao.updateUsername(userId, newUsername)

            // devolver usuario actualizado
            val updated = userDao.getUserById(userId)
            if (updated != null) {
                Result.success(updated)
            } else {
                Result.failure(Exception("No se pudo cargar el usuario actualizado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
