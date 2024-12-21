package com.project.dicodingevent.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.dicodingevent.data.database.FavoriteEvent
import com.project.dicodingevent.databinding.ItemFinishedEventBinding
import com.bumptech.glide.Glide

class FavoriteEventAdapter : ListAdapter<FavoriteEvent, FavoriteEventAdapter.ViewHolder>(
    DIFF_CALLBACK
) {

    class ViewHolder(private val binding: ItemFinishedEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: FavoriteEvent) {
            binding.tvItemName.text = event.name
            binding.tvEndTime.text = event.beginTime
            Glide.with(binding.imgItemPhoto.context)
                .load(event.imageLogo)
                .into(binding.imgItemPhoto)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemFinishedEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            val eventId = event.id
            val action = FavoriteFragmentDirections
                .actionNavigationFavoriteToDetailFragment(eventId)
            holder.itemView.findNavController().navigate(action)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEvent>() {
            override fun areItemsTheSame(
                oldItem: FavoriteEvent,
                newItem: FavoriteEvent
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FavoriteEvent,
                newItem: FavoriteEvent
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}