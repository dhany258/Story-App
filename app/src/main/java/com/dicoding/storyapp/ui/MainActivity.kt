package com.dicoding.storyapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.detail.DetailActivity
import com.dicoding.storyapp.ui.login.SignInActivity
import com.dicoding.storyapp.ui.maps.MapsActivity
import com.dicoding.storyapp.ui.stories.LoadingStateAdapter
import com.dicoding.storyapp.ui.stories.MainViewModel
import com.dicoding.storyapp.ui.stories.StoryAdapter
import com.dicoding.storyapp.ui.uploadStory.UploadStoryActivity
import com.dicoding.storyapp.utilis.ViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel: MainViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycleView()


    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getToken().observe(this@MainActivity) { token ->
                token?.let {
                    viewModel.getStories(it).observe(this@MainActivity) { pagingData ->
                        lifecycleScope.launch {
                            storyAdapter.submitData(pagingData)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecycleView(){
        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_STORY, story)
            startActivity(intent)
        }
        binding.recycleView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {storyAdapter}
            )
        }
    }

//    private fun showLoading(state: Boolean){
//        binding.progressBar.isVisible = state
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                performLogout()
                true
            }
            R.id.action_upload -> {
                startActivity(Intent(this, UploadStoryActivity::class.java))
                true
            }

            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        runBlocking {
            viewModel.logOut()
        }
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}