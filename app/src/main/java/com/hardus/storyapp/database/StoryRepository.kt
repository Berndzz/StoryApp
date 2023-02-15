package com.hardus.storyapp.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hardus.storyapp.api.ApiService
import com.hardus.storyapp.database.entity.EntityStory
import com.hardus.storyapp.database.room.StoryDatabase
import com.hardus.storyapp.response.FileUploadResponse
import com.hardus.storyapp.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val story: StoryDatabase,
    private val apiService: ApiService
) {

    fun getAllStories(token: String): Flow<PagingData<EntityStory>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(story, apiService, generateToken(token)),
            pagingSourceFactory = {
                story.storyDao().getStoryDB()
            }
        ).flow
    }

    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
    ): Flow<Result<FileUploadResponse>> = flow {
        try {
            val generateToken = generateToken(token)
            val response = apiService.addStory(generateToken, file, description, lat, lon)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    fun getAllStoriesWithLocation(token: String): Flow<Result<StoryResponse>> = flow {
        try {
            val generateToken = generateToken(token)
            val response = apiService.getStory(generateToken, size = 30, location = 1)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    private fun generateToken(token: String): String {
        return "Bearer $token"
    }
}