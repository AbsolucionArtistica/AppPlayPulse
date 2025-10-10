package com.example.appplaypulse_grupo4.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.entity.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUser_returnsCorrectId() = runTest {
        val user = User(username = "testuser", email = "test@example.com")
        val userId = userDao.insertUser(user)
        
        assertTrue("User ID should be greater than 0", userId > 0)
    }

    @Test
    fun getUserById_returnsCorrectUser() = runTest {
        val user = User(username = "testuser", email = "test@example.com")
        val userId = userDao.insertUser(user)
        
        val retrievedUser = userDao.getUserById(userId)
        
        assertNotNull("User should not be null", retrievedUser)
        assertEquals("Username should match", "testuser", retrievedUser?.username)
        assertEquals("Email should match", "test@example.com", retrievedUser?.email)
    }

    @Test
    fun updateUserHighScore_updatesCorrectly() = runTest {
        val user = User(username = "testuser", email = "test@example.com", highScore = 100)
        val userId = userDao.insertUser(user)
        
        userDao.updateUserHighScore(userId, 500)
        
        val updatedUser = userDao.getUserById(userId)
        assertEquals("High score should be updated", 500, updatedUser?.highScore)
    }

    @Test
    fun getTopUsers_returnsInDescendingOrder() = runTest {
        // Insert test users with different scores
        userDao.insertUser(User(username = "user1", email = "user1@test.com", highScore = 300))
        userDao.insertUser(User(username = "user2", email = "user2@test.com", highScore = 500))
        userDao.insertUser(User(username = "user3", email = "user3@test.com", highScore = 100))
        
        val topUsers = userDao.getTopUsers(3).first()
        
        assertEquals("Should return 3 users", 3, topUsers.size)
        assertEquals("First user should have highest score", 500, topUsers[0].highScore)
        assertEquals("Second user should have middle score", 300, topUsers[1].highScore)
        assertEquals("Third user should have lowest score", 100, topUsers[2].highScore)
    }

    @Test
    fun deleteAllUsers_clearsDatabase() = runTest {
        // Insert test users
        userDao.insertUser(User(username = "user1", email = "user1@test.com"))
        userDao.insertUser(User(username = "user2", email = "user2@test.com"))
        
        userDao.deleteAllUsers()
        
        val userCount = userDao.getUserCount()
        assertEquals("Database should be empty", 0, userCount)
    }
}
