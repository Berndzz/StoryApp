package com.hardus.storyapp.database.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hardus.storyapp.database.entity.EntityStory

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg storyData:EntityStory)

    @Query("SELECT * FROM story")
    fun getStoryDB():PagingSource<Int,EntityStory>

    @Query("DELETE FROM story")
    fun deleteAll()

}