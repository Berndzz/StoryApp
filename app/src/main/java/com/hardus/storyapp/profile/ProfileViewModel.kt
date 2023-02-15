package com.hardus.storyapp.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardus.storyapp.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) :
    ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun getNameUser(): Flow<String?> = authRepository.getNameUser()

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }

    fun saveNameUser(name: String) {
        viewModelScope.launch {
            authRepository.saveNameUser(name)
        }
    }

}