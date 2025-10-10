package com.example.appplaypulse_grupo4.database

/**
 * Custom exceptions for database operations
 */
class DatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)

class UserNotFoundException(userId: Long) : Exception("User with ID $userId not found")

class DuplicateUserException(username: String) : Exception("User with username '$username' already exists")

class InvalidScoreException(score: Int) : Exception("Invalid score: $score. Score must be >= 0")
