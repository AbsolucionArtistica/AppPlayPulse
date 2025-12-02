package com.example.appplaypulse_grupo4.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appplaypulse_grupo4.database.dao.UserDao
import com.example.appplaypulse_grupo4.database.dao.FriendDao
import com.example.appplaypulse_grupo4.database.dao.GameDao
import com.example.appplaypulse_grupo4.database.dao.UserGameDao
import com.example.appplaypulse_grupo4.database.dao.PostDao
import com.example.appplaypulse_grupo4.database.entity.User
import com.example.appplaypulse_grupo4.database.entity.FriendEntity
import com.example.appplaypulse_grupo4.database.entity.GameEntity
import com.example.appplaypulse_grupo4.database.entity.UserGameEntity
import com.example.appplaypulse_grupo4.database.entity.Post

@Database(
    entities = [
        User::class,
        FriendEntity::class,   // 👈 IMPORTANTE
        GameEntity::class,
        UserGameEntity::class,
        Post::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendDao(): FriendDao
    abstract fun gameDao(): GameDao
    abstract fun userGameDao(): UserGameDao
    abstract fun postDao(): PostDao
}
