package com.dicoding.asclepius.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.entity.CancerEntity
import com.dicoding.asclepius.databinding.ItemHistoryBinding

class ListCancerAdapter : ListAdapter<CancerEntity, ListCancerAdapter.CancerViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CancerViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CancerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CancerViewHolder, position: Int) {
        val cancer = getItem(position)
        holder.bind(cancer)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(getItem(holder.adapterPosition))
        }
    }

    class CancerViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cancer : CancerEntity){
            binding.tvCancerResult.text = "Cancer : ${cancer.result}"
            binding.ivHistoryImage.setImageURI(cancer.imageUri.toUri())
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CancerEntity>() {
            override fun areItemsTheSame(oldItem: CancerEntity, newItem: CancerEntity): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: CancerEntity, newItem: CancerEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: CancerEntity)
    }
}