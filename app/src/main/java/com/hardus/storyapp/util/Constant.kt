package com.hardus.storyapp.util

import androidx.datastore.preferences.core.stringPreferencesKey
import com.hardus.storyapp.BuildConfig

object Constant {
    const val BASE_URL : String = BuildConfig.URL
    val TOKEN = stringPreferencesKey("token")
    val NAME = stringPreferencesKey("name")
    const val INITIAL_PAGE_INDEX = 1
    const val EXTRA_TOKEN = "extra_token"
    const val EXTRA_DETAIL = "extra_detail"
    const val TAG = "CreateStoryActivity"
}