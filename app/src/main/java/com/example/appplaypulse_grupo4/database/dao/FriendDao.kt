package com.example.appplaypulse_grupo4.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appplaypulse_grupo4.database.entity.FriendEntity

@Dao
interface FriendDao {

    @Query("SELECT * FROM friends WHERE ownerUserId = :ownerId")
    suspend fun getFriendsForUser(ownerId: Long): List<FriendEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity): Long

    @Delete
    suspend fun deleteFriend(friend: FriendEntity)

    @Query("DELETE FROM friends WHERE ownerUserId = :ownerId")
    suspend fun deleteFriendsForUser(ownerId: Long)
}
