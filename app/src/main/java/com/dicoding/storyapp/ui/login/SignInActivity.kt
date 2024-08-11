package com.dicoding.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.databinding.ActivitySignInBinding
import com.dicoding.storyapp.ui.MainActivity
import com.dicoding.storyapp.ui.signup.SignUpActivity
import com.dicoding.storyapp.utilis.Preference
import com.dicoding.storyapp.utilis.ViewModelFactory
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val signInViewModel: SignInViewModel by viewModels{
        ViewModelFactory(this@SignInActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAction()
        playAnimation()

        binding.signUp.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupAction(){
        binding.apply {
            btnSignIn.setOnClickListener{
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()

                signInViewModel.signIn(email, password).observe(this@SignInActivity){
                    if (it != null){
                        when(it){
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Success -> {
                                showLoading(false)
                                processSignup(it.data)
                            }
                            is Result.Error ->{
                                showLoading(false)
                                Toast.makeText(this@SignInActivity, it.error, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            }
        }
    }

    private fun processSignup(data: LoginResponse){
        if (data.error == true) {
            Toast.makeText(this, "Gagal Sign In", Toast.LENGTH_LONG).show()
        } else {
            lifecycleScope.launch {
                Preference.saveToken(this@SignInActivity, data.loginResult?.token ?: "")
                Toast.makeText(this@SignInActivity, "Sign In berhasil!", Toast.LENGTH_LONG).show()
                navigateToMain()
            }
        }
    }


    private fun playAnimation(){
        val duration = 500L

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(duration)
        val titleDesc = ObjectAnimator.ofFloat(binding.tvLoginDes, View.ALPHA, 1f).setDuration(duration)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(duration)
        val inputEmail = ObjectAnimator.ofFloat(binding.inputEmailLayout, View.ALPHA, 1f).setDuration(duration)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(duration)
        val inputPassword = ObjectAnimator.ofFloat(binding.inputPasswordLayout, View.ALPHA, 1f).setDuration(duration)
        val btnSignIn = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(duration)
        val tvOr = ObjectAnimator.ofFloat(binding.tvOr, View.ALPHA, 1f).setDuration(duration)
        val signUp = ObjectAnimator.ofFloat(binding.signUp, View.ALPHA, 1f).setDuration(duration)

        AnimatorSet().apply{
            playSequentially(title, titleDesc, email, inputEmail, password, inputPassword, btnSignIn, tvOr, signUp)
            start()
        }
    }

    private fun showLoading(state: Boolean){
        binding.progressBar.isVisible = state
    }

    private fun navigateToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
