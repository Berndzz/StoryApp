package com.hardus.storyapp.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hardus.storyapp.R
import com.hardus.storyapp.database.entity.EntityStory
import com.hardus.storyapp.databinding.ItemRowStoryBinding
import com.hardus.storyapp.detail.DetailActivity
import com.hardus.storyapp.util.Constant.EXTRA_DETAIL
import com.hardus.storyapp.util.setImageFromUrl

class ListStoryAdapter :
    PagingDataAdapter<EntityStory, ListStoryAdapter.ListViewHolder>(DiffCallback) {


    inner class ListViewHolder(private var binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: EntityStory) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_baseline_tag_faces_24)
                    .into(CIVStory)
                tvName.text = story.name
                tvDetailCaption.text = story.description
                ivStory.setImageFromUrl(context,story.photoUrl)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivStory, "photo"),
                            Pair(tvName, "name"),
                            Pair(tvDetailCaption, "description"),
                        )
                    Intent(context, DetailActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, story)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<EntityStory>() {
            override fun areItemsTheSame(oldItem: EntityStory, newItem: EntityStory): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: EntityStory, newItem: EntityStory): Boolean {
                return oldItem == newItem
            }
        }
    }


}