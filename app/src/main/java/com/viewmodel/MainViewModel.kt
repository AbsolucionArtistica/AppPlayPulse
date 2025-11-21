package com.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appplaypulse_grupo4.database.AppDatabase
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.entity.Game
import com.example.appplaypulse_grupo4.database.entity.Achievement
import com.example.appplaypulse_grupo4.database.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val userRepository = UserRepository(
        database.userDao(),
        database.gameDao(),
        database.achievementDao()
    )
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _topUsers = MutableStateFlow<List<User>>(emptyList())
    val topUsers: StateFlow<List<User>> = _topUsers.asStateFlow()
    
    private val _userGames = MutableStateFlow<List<Game>>(emptyList())
    val userGames: StateFlow<List<Game>> = _userGames.asStateFlow()
    
    private val _gameAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val gameAchievements: StateFlow<List<Achievement>> = _gameAchievements.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadTopUsers()
    }
    
    // ============================================
    // USER OPERATIONS
    // ============================================
    
    fun registerUser(username: String, password: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = userRepository.syncUserWithServer(username, password, email)
                result.onSuccess { user ->
                    _currentUser.value = user
                    loadUserGames(user.id)
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al registrar usuario"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = userRepository.loginUserWithServer(username, password)
                result.onSuccess { user ->
                    _currentUser.value = user
                    loadUserGames(user.id)
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al iniciar sesión"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateUserScore(newScore: Int) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                try {
                    userRepository.updateScore(user.id, newScore)
                    // Refresh current user data
                    val updatedUser = userRepository.getUserById(user.id)
                    _currentUser.value = updatedUser
                    // Refresh leaderboard
                    loadTopUsers()
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                }
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
        _userGames.value = emptyList()
        _gameAchievements.value = emptyList()
        _errorMessage.value = null
    }
    
    // ============================================
    // GAME OPERATIONS
    // ============================================
    
    fun addGame(name: String, photoUrl: String = "", score: Int = 0) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                try {
                    userRepository.addGame(user.id, name, photoUrl, score)
                    loadUserGames(user.id)
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                }
            }
        }
    }
    
    fun updateGame(gameId: Long, name: String? = null, photoUrl: String? = null, score: Int? = null) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                try {
                    userRepository.updateGame(gameId, name, photoUrl, score)
                    loadUserGames(user.id)
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                }
            }
        }
    }
    
    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                try {
                    userRepository.deleteGame(gameId)
                    loadUserGames(user.id)
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                }
            }
        }
    }
    
    private fun loadUserGames(userId: Long) {
        viewModelScope.launch {
            try {
                userRepository.getUserGames(userId).collect { games ->
                    _userGames.value = games
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    // ============================================
    // ACHIEVEMENT OPERATIONS
    // ============================================
    
    fun addAchievement(gameId: Long, name: String, description: String = "") {
        viewModelScope.launch {
            try {
                userRepository.addAchievement(gameId, name, description)
                loadGameAchievements(gameId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteAchievement(achievementId: Long, gameId: Long) {
        viewModelScope.launch {
            try {
                userRepository.deleteAchievement(achievementId)
                loadGameAchievements(gameId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun loadGameAchievements(gameId: Long) {
        viewModelScope.launch {
            try {
                userRepository.getGameAchievements(gameId).collect { achievements ->
                    _gameAchievements.value = achievements
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    // ============================================
    // SYNC OPERATIONS
    // ============================================
    
    fun syncGamesFromServer(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = userRepository.syncGamesFromServer(userId)
                result.onSuccess {
                    loadUserGames(userId)
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al sincronizar juegos"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun syncAchievementsFromServer(gameId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = userRepository.syncAchievementsFromServer(gameId)
                result.onSuccess {
                    loadGameAchievements(gameId)
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al sincronizar logros"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadTopUsers() {
        viewModelScope.launch {
            try {
                userRepository.getTopUsers(10).collect { users ->
                    _topUsers.value = users
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}