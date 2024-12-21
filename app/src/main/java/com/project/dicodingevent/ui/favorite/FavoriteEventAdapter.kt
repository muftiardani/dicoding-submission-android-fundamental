package com.project.dicodingevent.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.dicodingevent.data.database.FavoriteEvent
import com.project.dicodingevent.databinding.ItemFinishedEventBinding

class FavoriteEventAdapter : ListAdapter<FavoriteEvent, FavoriteEventAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFinishedEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        setupItemClickListener(holder, event)
    }

    private fun setupItemClickListener(holder: ViewHolder, event: FavoriteEvent) {
        holder.itemView.setOnClickListener {
            val action = FavoriteFragmentDirections
                .actionNavigationFavoriteToDetailFragment(event.id)
            holder.itemView.findNavController().navigate(action)
        }
    }

    class ViewHolder(private val binding: ItemFinishedEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: FavoriteEvent) {
            with(binding) {
                tvItemName.text = event.name
                tvEndTime.text = event.beginTime

                Glide.with(imgItemPhoto.context)
                    .load(event.imageLogo)
                    .into(imgItemPhoto)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEvent>() {
            override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean =
                oldItem == newItem
        }
    }
}