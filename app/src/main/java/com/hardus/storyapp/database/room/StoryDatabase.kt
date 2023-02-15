package com.hardus.storyapp.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hardus.storyapp.database.entity.EntityRemoteKeys
import com.hardus.storyapp.database.entity.EntityStory

@Database(
    entities = [EntityStory::class, EntityRemoteKeys::class],
    version = 1,
    exportSchema = false
)

abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeyDao(): RemoteKeysDao
}