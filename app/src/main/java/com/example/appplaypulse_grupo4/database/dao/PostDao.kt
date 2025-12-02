package com.example.appplaypulse_grupo4.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appplaypulse_grupo4.database.dto.FeedItem
import com.example.appplaypulse_grupo4.database.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Query(
        """
        SELECT 
            p.id AS id,
            u.username AS username,
            p.content AS content,
            p.location AS location,
            p.link AS link,
            p.imageUri AS imageUri,
            p.createdAt AS createdAt
        FROM posts AS p
        INNER JOIN users AS u ON u.id = p.userId
        ORDER BY p.createdAt DESC
        """
    )
    fun getFeed(): Flow<List<FeedItem>>

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}
