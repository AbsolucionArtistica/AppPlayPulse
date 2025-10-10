package com.example.appplaypulse_grupo4.security

/**
 * SQL Injection Security Guidelines for Room Database
 * 
 * ✅ SECURE PRACTICES:
 * 
 * 1. Use Room's parameterized queries with : syntax
 *    @Query("SELECT * FROM users WHERE username = :username")
 *    - Room automatically escapes these parameters
 * 
 * 2. Use typed parameters
 *    suspend fun getUserById(userId: Long): User?
 *    - Type safety prevents injection
 * 
 * 3. Validate input at repository level
 *    fun searchUsers(query: String): List<User> {
 *        val safeQuery = query.replace("[^a-zA-Z0-9_]", "")
 *        return dao.searchByPattern("%$safeQuery%")
 *    }
 * 
 * ❌ AVOID THESE PATTERNS:
 * 
 * 1. Raw SQL string concatenation
 *    "SELECT * FROM users WHERE name = '$input'" // NEVER DO THIS
 * 
 * 2. Dynamic query building with user input
 *    val query = "SELECT * FROM users WHERE $userColumn = '$userValue'"
 * 
 * 3. Unvalidated user input in queries
 *    @Query("SELECT * FROM users WHERE username LIKE '$pattern'") // WRONG
 *    Use @Query("SELECT * FROM users WHERE username LIKE :pattern") // CORRECT
 * 
 * 🔒 ADDITIONAL SECURITY MEASURES:
 * 
 * 1. Input validation at UI level
 * 2. Input sanitization at repository level  
 * 3. Use allowlists for dynamic column names
 * 4. Limit query results to prevent data leakage
 * 5. Regular security audits of database queries
 */

object DatabaseSecurityGuidelines {
    
    /**
     * Sanitizes user input for database queries
     * Removes potentially dangerous characters
     */
    fun sanitizeInput(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9_@.-]"), "")
                   .take(50) // Limit length
    }
    
    /**
     * Validates username format
     */
    fun isValidUsername(username: String): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))
    }
    
    /**
     * Validates email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }
    
    /**
     * Allowed columns for sorting (prevent column injection)
     */
    val ALLOWED_SORT_COLUMNS = setOf("username", "highScore", "level", "createdAt")
    
    /**
     * Validates sort column to prevent injection
     */
    fun isValidSortColumn(column: String): Boolean {
        return column in ALLOWED_SORT_COLUMNS
    }
}
