package com.example.appplaypulse_grupo4.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appplaypulse_grupo4.database.entity.Post
import com.example.appplaypulse_grupo4.database.dto.FeedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Query("""
        SELECT 
            p.id          AS id,
            p.content     AS content,
            p.createdAt   AS createdAt,
            u.username    AS username,
            p.location    AS location,
            p.link        AS link
        FROM posts p
        INNER JOIN users u ON u.id = p.userId
        ORDER BY p.createdAt DESC
    """)
    fun getFeed(): Flow<List<FeedItem>>
}
