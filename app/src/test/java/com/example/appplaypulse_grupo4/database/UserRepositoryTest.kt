package com.example.appplaypulse_grupo4.database

import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UserRepositoryTest {

    @MockK
    lateinit var userDao: UserDao

    private lateinit var repository: UserRepository

    @BeforeEach
    fun setup() {
        repository = UserRepository(userDao)
    }

    @Test
    fun `login succeeds when dao finds user`() = runTest {
        val storedUser = sampleUser(username = "playerOne")
        coEvery { userDao.login("playerOne", "Aa1!aaaa") } returns storedUser

        val result = repository.login("playerOne", "Aa1!aaaa")

        assertTrue(result.isSuccess)
        coVerify { userDao.login("playerOne", "Aa1!aaaa") }
    }

    @Test
    fun `register rejects duplicated username`() = runTest {
        val existing = sampleUser(id = 99, username = "repeated")
        coEvery { userDao.existsUsername("repeated") } returns existing
        coEvery { userDao.existsEmail(any()) } returns null
        coEvery { userDao.existsPhone(any()) } returns null

        val result = repository.registerUser(
            nombre = "Ana",
            apellido = "Lopez",
            edad = 22,
            email = "ana@test.com",
            phone = "+56 9 12345678",
            username = "repeated",
            password = "Aa1!aaaa"
        )

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `register inserts when validations pass`() = runTest {
        coEvery { userDao.existsUsername(any()) } returns null
        coEvery { userDao.existsEmail(any()) } returns null
        coEvery { userDao.existsPhone(any()) } returns null
        coEvery { userDao.insertUser(any()) } returns 5L

        val result = repository.registerUser(
            nombre = "Beto",
            apellido = "Gomez",
            edad = 25,
            email = "beto@test.com",
            phone = "+56 9 87654321",
            username = "BetoUser",
            password = "Aa1!aaaa"
        )

        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrNull()?.id)
        coVerify { userDao.insertUser(any()) }
    }

    private fun sampleUser(
        id: Long = 1L,
        username: String = "user",
        email: String = "user@test.com"
    ) = User(
        id = id,
        nombre = "Test",
        apellido = "User",
        edad = 20,
        email = email,
        phone = "+56 9 11111111",
        username = username,
        password = "Aa1!aaaa"
    )
}
