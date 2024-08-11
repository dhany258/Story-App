package com.dicoding.storyapp.utilis

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.ui.signup.SignUpViewModel
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.ui.login.SignInViewModel
import com.dicoding.storyapp.ui.maps.MapViewModel
import com.dicoding.storyapp.ui.stories.MainViewModel
import com.dicoding.storyapp.ui.uploadStory.UploadStoryViewModel


class ViewModelFactory (private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                SignInViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(UploadStoryViewModel::class.java) -> {
                UploadStoryViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(Injection.provideRepository(context)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}