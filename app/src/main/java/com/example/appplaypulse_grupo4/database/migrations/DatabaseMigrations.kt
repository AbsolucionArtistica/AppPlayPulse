package com.example.appplaypulse_grupo4.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for schema changes
 * Add new migrations here when you update the database schema
 */
object DatabaseMigrations {
    
    // Example migration from version 1 to 2
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Add a new column
            // database.execSQL("ALTER TABLE users ADD COLUMN avatar_url TEXT")
        }
    }
    
    // Example migration from version 2 to 3
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Add a new table
            // database.execSQL("CREATE TABLE achievements (id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, user_id INTEGER NOT NULL)")
        }
    }
    
    // Array of all migrations
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3
    )
}
