package com.dicoding.anarki.ui.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.databinding.ItemRowBinding


class RecentAdapter : PagedListAdapter<PredictEntity, RecentAdapter.ListViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PredictEntity>() {
            override fun areItemsTheSame(oldItem: PredictEntity, newItem: PredictEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PredictEntity, newItem: PredictEntity): Boolean {
                return oldItem == newItem
            }
        }
    }


    fun getSwipedData(swipedPosition: Int): PredictEntity? = getItem(swipedPosition)

    inner class ListViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(predict: PredictEntity) {
            val image = predict.image!!.toUri()

            with(binding) {
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(image)
                    .into(imgFile)

                tvAddicted.text = predict.result
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val history = getItem(position)
        if (history != null) {
            holder.bind(history)
        }
    }

}