package com.hardus.storyapp.authentication

import com.hardus.storyapp.api.ApiService
import com.hardus.storyapp.authentication.preferences.AuthPreferencesData
import com.hardus.storyapp.response.LoginResponse
import com.hardus.storyapp.response.SignUpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val authPreferencesData: AuthPreferencesData
) {
    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun signUpUser(
        name: String,
        email: String,
        password: String
    ): Flow<Result<SignUpResponse>> = flow {
        try {
            val response = apiService.signUp(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthToken(token: String) {
        authPreferencesData.saveAuthToken(token)
    }

    suspend fun saveNameUser(name: String) {
        authPreferencesData.saveNameUser(name)
    }


    fun getNameUser(): Flow<String?> = authPreferencesData.getNameUser()

    fun getAuthToken(): Flow<String?> = authPreferencesData.getAuthToken()

}