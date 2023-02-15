package com.hardus.storyapp.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hardus.storyapp.database.entity.EntityRemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertAll(remoteKeys: List<EntityRemoteKeys>)

   @Query("SELECT * FROM remote_keys WHERE id = :id ")
   suspend fun getRemoteKeysId(id:String):EntityRemoteKeys?

   @Query("DELETE FROM remote_keys")
   suspend fun deleteRemoteKeys()
}