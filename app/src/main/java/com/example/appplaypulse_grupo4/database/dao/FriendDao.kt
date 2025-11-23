package com.example.appplaypulse_grupo4.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appplaypulse_grupo4.database.entity.FriendEntity

@Dao
interface FriendDao {

    @Query("SELECT * FROM friends WHERE ownerUserId = :userId")
    suspend fun getFriendsForUser(userId: Long): List<FriendEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Query("DELETE FROM friends WHERE ownerUserId = :userId")
    suspend fun deleteFriendsForUser(userId: Long)
}
