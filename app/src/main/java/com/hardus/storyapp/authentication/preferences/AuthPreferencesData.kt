package com.hardus.storyapp.authentication.preferences


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.hardus.storyapp.util.Constant.NAME
import com.hardus.storyapp.util.Constant.TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferencesData @Inject constructor(private val dataStore: DataStore<Preferences>) {

    //ambil
    fun getAuthToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN]
        }
    }

    fun getNameUser(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[NAME]
        }
    }

    //simpan
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun saveNameUser(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

}

