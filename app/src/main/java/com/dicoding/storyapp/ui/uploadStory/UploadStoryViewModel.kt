package com.dicoding.storyapp.ui.uploadStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.response.PostStoryResponse
import com.dicoding.storyapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody): LiveData<Result<PostStoryResponse>>{
        return storyRepository.postStory(token, file, description)
    }

    fun getToken(): LiveData<String?> {
        return storyRepository.getToken()
    }
}