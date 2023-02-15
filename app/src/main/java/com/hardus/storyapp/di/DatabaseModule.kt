package com.hardus.storyapp.di

import android.content.Context
import androidx.room.Room
import com.hardus.storyapp.database.room.RemoteKeysDao
import com.hardus.storyapp.database.room.StoryDao
import com.hardus.storyapp.database.room.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideStoryDao(story: StoryDatabase): StoryDao = story.storyDao()

    @Provides
    fun provideRemoteKeysDao(story: StoryDatabase): RemoteKeysDao =
        story.remoteKeyDao()

    @Provides
    @Singleton
    fun providedStoryDb(@ApplicationContext context: Context): StoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java,
            "story_app_db"
        ).build()
    }

}