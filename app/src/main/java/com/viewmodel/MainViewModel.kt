package com.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.appplaypulse_grupo4.database.AppDatabase
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // 🗄️ Instancia de Room (usa tu AppDatabase)
    private val db: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "playpulse.db"
    ).build()

    private val userRepository = UserRepository(db.userDao())

    // 👤 Usuario actual en sesión
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // ⏳ Cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ⚠️ Mensaje de error/estado rápido
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // =========================================================
    // 🔹 REGISTRO DE USUARIO (usa registerUser del repositorio)
    // =========================================================
    fun registerUser(
        nombre: String,
        apellido: String,
        edad: Int,
        email: String,
        phone: String,
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = null

            val result = userRepository.registerUser(
                nombre = nombre,
                apellido = apellido,
                edad = edad,
                email = email,
                phone = phone,
                username = username,
                password = password
            )

            result
                .onSuccess { user ->
                    _currentUser.value = user
                    _statusMessage.value = "Registro exitoso"
                }
                .onFailure { ex ->
                    _statusMessage.value = ex.message ?: "Error al registrar usuario"
                }

            _isLoading.value = false
        }
    }

    // =========================================================
    // 🔹 LOGIN (usa login del repositorio)
    // =========================================================
    fun login(field: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = null

            val result = userRepository.login(field, password)

            result
                .onSuccess { user ->
                    _currentUser.value = user
                    _statusMessage.value = "Bienvenid@ ${user.username}"
                }
                .onFailure { ex ->
                    _statusMessage.value = ex.message ?: "Usuario o contraseña incorrectos"
                }

            _isLoading.value = false
        }
    }

    // =========================================================
    // 🔹 CERRAR SESIÓN
    // =========================================================
    fun logout() {
        _currentUser.value = null
        _statusMessage.value = "Sesión cerrada"
    }

    // Opcional: limpiar mensajes luego de mostrarlos en UI
    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
