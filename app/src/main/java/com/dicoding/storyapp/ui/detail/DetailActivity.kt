package com.dicoding.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Detail Story"
            setDisplayHomeAsUpEnabled(true)
        }


        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        story?.let {
            binding.tvName.text = it.name
            binding.tvDescription.text = it.description
            Glide.with(this)
                .load(story.photoUrl)
                .centerCrop()
                .into(binding.imageDetail)
        }
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


    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}