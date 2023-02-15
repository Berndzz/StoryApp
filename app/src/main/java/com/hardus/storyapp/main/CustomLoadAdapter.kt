package com.hardus.storyapp.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hardus.storyapp.databinding.LayoutLoadingStoryBinding

class CustomLoadAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<CustomLoadAdapter.LoadingStateViewHolder>() {

    class LoadingStateViewHolder(
        private val binding: LayoutLoadingStoryBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.tvErrorMsg.text = "Data Kosong"
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.btnRetryButton.isVisible = loadState is LoadState.Error
            binding.tvErrorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding =
            LayoutLoadingStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

}