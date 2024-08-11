package com.dicoding.storyapp.di


import android.content.Context
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.api.ApiConfig


object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService, context)
    }
}