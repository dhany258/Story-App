package com.dicoding.storyapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ui.login.SignInActivity
import com.dicoding.storyapp.ui.login.SignInViewModel
import com.dicoding.storyapp.utilis.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {
    private val signInViewModel: SignInViewModel by viewModels {
        ViewModelFactory(this@SplashActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)

        supportActionBar?.hide()

        val splashScreenDuration = 1000L
        lifecycleScope.launch {
            delay(splashScreenDuration)

            signInViewModel.getToken().observe(this@SplashActivity) { token ->
                if (token.isNullOrEmpty()) {
                    val intent = Intent(this@SplashActivity, SignInActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
        }
    }
}