package com.hardus.storyapp.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hardus.storyapp.R
import com.hardus.storyapp.database.entity.EntityStory
import com.hardus.storyapp.databinding.ActivityDetailBinding
import com.hardus.storyapp.util.Constant.EXTRA_DETAIL

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)


        supportActionBar?.title = "Detail Story"

        val story = intent.getParcelableExtra<EntityStory>(EXTRA_DETAIL)
        getDetailStory(story)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getDetailStory(story: EntityStory?) {
        if (story != null) {
            detailBinding.apply {
                tvDetailName.text = story.name
                tvDetailDescription.text = story.description
                supportActionBar?.title = story.name + " story"
                Glide
                    .with(this@DetailActivity)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.image_loading_placeholder)
                    .error(R.drawable.image_load_error)
                    .into(ivDetailUser)
            }
        }
    }
}