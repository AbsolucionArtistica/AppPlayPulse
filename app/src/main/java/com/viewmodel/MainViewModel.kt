package com.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appplaypulse_grupo4.database.AppDatabase
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val userRepository = UserRepository(database.userDao())
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _topUsers = MutableStateFlow<List<User>>(emptyList())
    val topUsers: StateFlow<List<User>> = _topUsers.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadTopUsers()
    }
    
    fun createUser(username: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = userRepository.createUser(username, email)
                val newUser = userRepository.getUserById(userId)
                _currentUser.value = newUser
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loginUser(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getUserByUsername(username)
                _currentUser.value = user
            } catch (e: Exception) {
                // Handle error
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
                    // Handle error
                }
            }
        }
    }
    
    private fun loadTopUsers() {
        viewModelScope.launch {
            userRepository.getTopUsers(10).collect { users ->
                _topUsers.value = users
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
    }
}