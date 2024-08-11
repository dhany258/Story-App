package com.dicoding.storyapp.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.response.ListStoryItem


class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getStory(token).cachedIn(viewModelScope)
    }

    fun getToken(): LiveData<String?> {
        return storyRepository.getToken()
    }

    suspend fun logOut() = storyRepository.logOut()
}