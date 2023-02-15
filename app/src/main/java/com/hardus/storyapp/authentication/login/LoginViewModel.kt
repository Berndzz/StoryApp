package com.hardus.storyapp.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>> =
        authRepository.loginUser(email, password)

    fun saveToken(token: String) {
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

