package com.hardus.storyapp.location

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.database.StoryRepository
import com.hardus.storyapp.response.StoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) :
    ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun getAllStories(token: String): Flow<Result<StoryResponse>> =
        storyRepository.getAllStoriesWithLocation(token)

}