package com.hardus.storyapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.database.StoryRepository
import com.hardus.storyapp.database.entity.EntityStory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {


    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun getAllStories(token: String): LiveData<PagingData<EntityStory>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope).asLiveData()
}