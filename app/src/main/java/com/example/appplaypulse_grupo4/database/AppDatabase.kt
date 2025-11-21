package com.example.appplaypulse_grupo4.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.dao.GameDao
import com.example.appplaypulse_grupo4.database.dao.AchievementDao
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.entity.Game
import com.example.appplaypulse_grupo4.database.entity.Achievement
import com.example.appplaypulse_grupo4.database.migrations.DatabaseMigrations

@Database(
    entities = [User::class, Game::class, Achievement::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun gameDao(): GameDao
    abstract fun achievementDao(): AchievementDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
                .fallbackToDestructiveMigration() // Remove this in production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
