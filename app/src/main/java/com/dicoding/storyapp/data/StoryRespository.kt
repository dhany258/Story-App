package com.dicoding.storyapp.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.PostStoryResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.data.response.StoryResponse
import com.dicoding.storyapp.ui.stories.StoryPagingSource
import com.dicoding.storyapp.utilis.Preference
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryRepository(private val apiService: ApiService, private val context: Context) {

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return liveData {
            val pager = Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                pagingSourceFactory = {
                    StoryPagingSource(apiService, token)
                }
            )
            emitSource(pager.liveData)
        }
    }

    fun postSignUp(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("SignUpViewModel", "postSignUp: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postSignIn(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error == false) {
                Preference.saveToken(context, response.loginResult?.token ?: "")
            }
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("SignInViewModel", "postSignIn: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getToken(): LiveData<String?> = liveData {
        val token = Preference.getToken(context).first()
        Log.d("StoryRepository", "Token: $token")
        emit(token)
    }

    fun postStory(token: String, file: MultipartBody.Part, description: RequestBody): LiveData<Result<PostStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val apiService: ApiService = ApiConfig.getApiServiceWithAuth(token)
            val response = apiService.postStory("Bearer $token", file, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("StoryRepository", "Error uploading story: ${e.message}", e)
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = Preference.getToken(context).first()
            val response = apiService.getStoriesWithLocation(1, "Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("ListStoryViewModel", "getStoriesWithLocation: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }



    suspend fun logOut() {
        Preference.logOut(context)
    }
}