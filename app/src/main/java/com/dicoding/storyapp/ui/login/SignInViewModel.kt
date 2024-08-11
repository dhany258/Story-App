package com.dicoding.storyapp.ui.login

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository

class SignInViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun signIn(email: String, password: String) = storyRepository.postSignIn(email, password)
    fun getToken() = storyRepository.getToken()
}