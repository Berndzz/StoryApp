package com.hardus.storyapp.addstory

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.database.StoryRepository
import com.hardus.storyapp.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> =
        storyRepository.uploadImage(token, file, description, lat, lon)
}