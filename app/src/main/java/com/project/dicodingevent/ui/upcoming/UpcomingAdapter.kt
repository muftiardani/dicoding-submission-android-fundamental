package com.project.dicodingevent.ui.upcoming

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.dicodingevent.R
import com.project.dicodingevent.data.response.ListEventsItem
import com.project.dicodingevent.databinding.ItemUpcomingEventBinding
import com.bumptech.glide.Glide

class UpcomingAdapter : ListAdapter<ListEventsItem, UpcomingAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemUpcomingEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(eventsItem: ListEventsItem) {
            binding.apply {
                tvItemName.text = eventsItem.name
                tvLocation.text = eventsItem.cityName
             }

            val context = binding.root.context
            val registrantsQuotaText = context.getString(R.string.registrants_quota, eventsItem.registrants, eventsItem.quota)
            binding.tvQuota.text = registrantsQuotaText

            Glide.with(binding.root.context)
                .load(eventsItem.imageLogo)
                .into(binding.imgItemPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpcomingEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            val eventId = event.id.toString()
            val action = UpcomingFragmentDirections
                .actionNavigationUpcomingToDetailFragment(eventId)

            holder.itemView.findNavController().navigate(action)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}