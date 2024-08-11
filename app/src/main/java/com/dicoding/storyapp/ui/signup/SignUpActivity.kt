package com.dicoding.storyapp.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.databinding.ActivitySignUpBinding

import com.dicoding.storyapp.ui.login.SignInActivity
import com.dicoding.storyapp.utilis.ViewModelFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels{
        ViewModelFactory(this@SignUpActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAction()

        binding.signIn.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupAction(){
        binding.apply {
            btnSignUp.setOnClickListener{
                val name = inputName.text.toString()
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()

                signUpViewModel.signUp(name, email, password).observe(this@SignUpActivity){
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
                                Toast.makeText(this@SignUpActivity, it.error, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun processSignup(data: RegisterResponse){
        if (data.error == true){
            Toast.makeText(this, "Gagal Sign Up", Toast.LENGTH_LONG).show()
        } else{
            Toast.makeText(this, "Sign Up berhasil, silahkan login!", Toast.LENGTH_LONG).show()
            navigateToSignIn()
        }
    }

    private fun showLoading(state: Boolean){
        binding.progressBar.isVisible = state
    }

    private fun navigateToSignIn(){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}