package com.example.appplaypulse_grupo4.database

import android.content.Context
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseHelper(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val userRepository = UserRepository(database.userDao())
    private val scope = CoroutineScope(Dispatchers.IO)
    
    fun insertSampleData() {
        scope.launch {
            // Check if database is empty
            val existingUsers = userRepository.getAllUsers()
            
            // Insert sample users if database is empty
            val sampleUsers = listOf(
                User(username = "Player1", email = "player1@example.com", highScore = 1500, level = 2),
                User(username = "Player2", email = "player2@example.com", highScore = 2300, level = 3),
                User(username = "Player3", email = "player3@example.com", highScore = 800, level = 1),
                User(username = "Player4", email = "player4@example.com", highScore = 3200, level = 4),
                User(username = "Player5", email = "player5@example.com", highScore = 1200, level = 2)
            )
            
            sampleUsers.forEach { user ->
                userRepository.insertUser(user)
            }
        }
    }
    
    fun clearAllData() {
        scope.launch {
            userRepository.deleteAllUsers()
        }
    }
}
