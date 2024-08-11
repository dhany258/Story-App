package com.dicoding.storyapp.ui.uploadStory

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityUploadStoryBinding
import com.dicoding.storyapp.ui.MainActivity
import com.dicoding.storyapp.utilis.ViewModelFactory
import com.dicoding.storyapp.utilis.getImageUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream


class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private val viewModel: UploadStoryViewModel by viewModels { ViewModelFactory(this) }
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGalery.setOnClickListener{
            startGallery()
        }

        binding.btnCamera.setOnClickListener{
            startCamera()
        }

        binding.btnUpload.setOnClickListener{
            uploadStory()
        }

        supportActionBar?.apply {
            title = "Upload story"
            setDisplayHomeAsUpEnabled(true)
        }

    }

    private fun startGallery(){
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri: Uri? ->
        if ( uri != null){
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo picker", "No media selected")
        }
    }

    private fun startCamera(){
        currentImageUri = getImageUri(this)
        launchCamera.launch(currentImageUri!!)
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        showImage()
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imagePost.setImageURI(it)
        }
    }

    private fun uploadStory(){
        val descriptionText = binding.edtDescription.text.toString()
        if (descriptionText.isBlank() || currentImageUri == null){
            Toast.makeText(this, "Please add a description and select an image.", Toast.LENGTH_SHORT).show()
            return
        }

        val description = descriptionText.toRequestBody("text/plain".toMediaTypeOrNull())
        val file = uriToFile(currentImageUri!!)

        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

        viewModel.getToken().observe(this) { token ->
            if (token != null) {
                viewModel.uploadStory(token, multipartBody, description).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, result.data.message, Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, result.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun uriToFile(selectedImageUri: Uri): File {
        val contentResolver = contentResolver
        val myFile = File.createTempFile("temp_image", ".jpg", cacheDir)
        val inputStream = contentResolver.openInputStream(selectedImageUri) ?: return myFile
        val outputStream = FileOutputStream(myFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return myFile
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("currentImageUri", currentImageUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentImageUri = savedInstanceState.getParcelable("currentImageUri")
        showImage()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                onBackPressedDispatcher
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}