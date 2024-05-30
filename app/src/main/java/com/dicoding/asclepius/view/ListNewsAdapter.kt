package com.dicoding.asclepius.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.entity.NewsEntity
import com.dicoding.asclepius.databinding.ItemNewsBinding

class ListNewsAdapter : ListAdapter<NewsEntity, ListNewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(getItem(holder.adapterPosition))
        }
    }

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news : NewsEntity){
            Glide.with(binding.root)
                .load(news.imageUrl)
                .into(binding.ivNewsImage)

            binding.tvNewsTitle.text = news.title
            binding.tvNewsPublisher.text = news.publisher
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsEntity>() {
            override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                return oldItem.url == newItem.url
            }
            override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: NewsEntity)
    }
}