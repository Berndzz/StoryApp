package com.hardus.storyapp.authentication.signup

import androidx.lifecycle.ViewModel
import com.hardus.storyapp.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun signUpUser(name: String, email: String, password: String) =
        authRepository.signUpUser(name, email, password)
}

